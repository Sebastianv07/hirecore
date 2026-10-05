# Hirecore

Aplicación Spring Boot que modela el proceso de selección de un candidato con los patrones **Command**, **State** (en su variante de **máquina de estados dirigida por tabla**), **Template Method**, **Memento**, **Observer** y **Strategy**, más un registro de estados (**Registry**) y **eventos de dominio**.

El diagrama de clases, organizado por patrones, está en `Diagrama_Clases_HireCore.excalidraw` (se abre con la extensión Excalidraw de VS Code o en excalidraw.com). Los supuestos frente a la información incompleta de RRHH están en `SUPUESTOS.md`.

## Integrantes

- Alexander Marin Villa
- Leidy Melissa Trejos Pamplona
- Sebastian Vargas Guarin
- Fabian Andres Muñoz Camayo

## Cómo ejecutar las pruebas

Requiere Java 21.

```bash
./mvnw test
```

Las pruebas reemplazan a la antigua demostración por consola: recorren el mismo flujo, pero verifican el resultado en lugar de solo imprimirlo.

## Cómo funciona un cambio de estado

Nadie cambia el estado del candidato a mano. Cada movimiento es un `CambiarEstadoCommand` que se entrega a `GestorDeCandidato`, que siempre sigue los mismos pasos:

1. Toma una foto del candidato (`crearMemento`).
2. Ejecuta el comando: el candidato llama a `transicionarA`, y `EstadoBase` consulta en `ReglasTransicion` si el destino está entre los permitidos para el estado actual.
3. Si todo salió bien, guarda la foto en `HistorialCambios` (una pila por candidato).
4. Publica los eventos que acumuló el candidato a través de `PublicarEventos`.

Si la transición no está permitida se lanza `TransicionEstadoInvalida`: no se guarda la foto ni se avisa a nadie.

Para deshacer, `GestorDeCandidato.deshacer(candidato, autor)` saca la última foto de ese candidato y el candidato se restaura. Queda un `CambioRevertido` con quién lo deshizo y cuándo.

## Quién se entera de qué

`BusEventos` le pregunta a cada observador `leInteresa(evento)` y solo le entrega lo que le interesa. Si un observador falla, los demás igual reciben el evento.

| Observador | Le interesa | Eventos |
|---|---|---|
| `NotificarReclutador` | Todo | `EstadoCambiado`, `CambioRevertido`, `OfertaEmitida`, `CandidatoContratado` |
| `ActualizarPortalCandidato` | `EventoDeProgreso` | `EstadoCambiado`, `CambioRevertido` |
| `NotificarGerente` | `HitoDeDecision` | `OfertaEmitida`, `CandidatoContratado` |
| `NotificarNomina` | `CandidatoContratado` | `CandidatoContratado` |

`OfertaEmitida` y `CandidatoContratado` los emiten los propios estados al entrar (`EstadoOferta.alEntrar`, `EstadoContratado.alEntrar`). Los observadores avisan a través de `CanalNotificacion`; hoy la implementación es `CanalRegistro`, que escribe en el log.

## Transiciones

Son la tabla de `ReglasTransicion`, el único lugar donde está el grafo. Las clases de estado no la conocen.

```
APLICADO        → ENTREVISTA | RECHAZADO
ENTREVISTA      → PRUEBA_TECNICA | REFERENCIA | OFERTA | RECHAZADO
PRUEBA_TECNICA  → REFERENCIA | OFERTA | RECHAZADO
REFERENCIA      → PRUEBA_TECNICA | OFERTA | RECHAZADO
OFERTA          → CONTRATADO | RECHAZADO
CONTRATADO      → (final)
RECHAZADO       → (final)
```

## Qué verifican las pruebas

| Prueba | Qué demuestra |
|---|---|
| `ContratoEstadosTest` | Contrato de todos los estados registrados: códigos únicos, cada estado declarado en la tabla y cada código de la tabla registrado, todo estado alcanzable desde `APLICADO`, sin callejones sin salida, y toda transición no permitida lanza `TransicionEstadoInvalida` (Liskov). Revisa automáticamente cualquier estado nuevo. |
| `CandidatoTest` | Transiciones, eventos que emiten los estados, restauración desde una foto y protección contra fotos de otro candidato. |
| `GestorDeCandidatoTest` | Deshacer un rechazo por error, historial por candidato, deshacer varios pasos y que un salto inválido no guarde ni publique nada. |
| `BusEventosTest` | Entrega por interés, aislamiento de fallas, suscribir y desuscribir. |
| `ObservadoresTest` | A qué eventos atiende cada interesado y por qué canal avisa. |
| `HirecoreIntegracionTest` | Recorrido completo con Spring: cada interesado recibe solo lo que le corresponde. |

## Cómo extender

- **Nueva etapa:** una clase que hereda de `EstadoBase` con `@Component`, y dos filas en la tabla de `ReglasTransicion`: la del estado nuevo y la del estado desde el que se llega a él. `CatalogoEstados` la registra sola y ninguna clase de estado existente se modifica. Si no se conecta, `ContratoEstadosTest` falla avisando que no es alcanzable.
- **Nuevo interesado:** una clase que implementa `ObservadorEvento` con `@Component`. `BusEventos` la recibe sola.
- **Nuevo canal:** una clase que implementa `CanalNotificacion`.
