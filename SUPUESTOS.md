# Supuestos

La conversación con RRHH confirma la intención, no el detalle. Aquí se declara qué se asumió donde no hay certeza, y qué pieza de la arquitectura absorbe esa incertidumbre para no tener que reescribir el flujo principal cuando el negocio concrete la regla.

## 1. Alcance de “deshacer”

**Qué no se sabe.** RRHH pide revertir el último cambio (un rechazo aplicado al candidato equivocado). Desarrollo pregunta si es solo el último o cualquier punto del historial. La respuesta: *“con el último nos basta por ahora, pero no me sorprendería que más adelante pidamos poder ir más atrás.”*

**Qué se asumió.** El requisito vigente es deshacer **solo el último cambio** de un candidato. El diseño, sin embargo, no implementa un “deshacer de un solo nivel”: antes de cada cambio se guarda una foto del candidato, y las fotos se apilan en un historial **por candidato**. Deshacer no es una transición del negocio sino una restauración: si lo fuera, nunca se podría deshacer un rechazo, porque `RECHAZADO` es un estado final.

**Qué resuelve la arquitectura.** Memento: `Candidato` crea su foto (`MementoCandidato`) y sabe restaurarse; `HistorialCambios` las guarda sin conocer su contenido; los comandos no saben deshacerse. Hoy `GestorDeCandidato.deshacer()` restaura la última foto del candidato. Si RRHH pide ir más atrás, se extraen más fotos de la misma pila: no hay que rediseñar `Candidato`, ni los estados, ni las notificaciones. Cada restauración emite `CambioRevertido` con quién la hizo y cuándo.

## 2. Cuándo le interesa un cambio al gerente

**Qué no se sabe.** RRHH dice que el gerente *“solo le importa cuando ya hay algo concreto que decidir, no los pasos intermedios”*. No define qué cuenta como “concreto”.

**Qué se asumió.** Hay algo concreto que decidir en dos momentos:

- **OFERTA** — hay que aprobar o no enviar una oferta.
- **CONTRATADO** — hay que confirmar el cierre de la vacante.

Los pasos intermedios (aplicado, entrevista, prueba técnica, referencias) son operativos del reclutador, no del gerente.

**Qué resuelve la arquitectura.** Los dos momentos son eventos propios (`OfertaEmitida`, `CandidatoContratado`) que emiten los estados al entrar, y ambos pertenecen a la categoría `HitoDeDecision`. `NotificarGerente` solo se interesa por esa categoría, y `GestorDeCandidato` no conoce al gerente ni esa regla. Si “concreto” pasa a incluir, por ejemplo, el resultado de referencias, basta con un evento nuevo que sea `HitoDeDecision`: el gerente lo recibe sin modificarse.

## 3. El proceso de selección todavía no está cerrado

**Qué no se sabe.** RRHH pide prueba técnica y verificación de referencias, y avisa que *“seguramente en unos meses agreguemos alguna más — todavía estamos definiendo el proceso completo.”* No hay lista cerrada de etapas ni de transiciones futuras.

**Qué se asumió.** El conjunto actual es:

```
APLICADO → ENTREVISTA | RECHAZADO
ENTREVISTA → PRUEBA_TECNICA | REFERENCIA | OFERTA | RECHAZADO
PRUEBA_TECNICA → REFERENCIA | OFERTA | RECHAZADO
REFERENCIA → PRUEBA_TECNICA | OFERTA | RECHAZADO
OFERTA → CONTRATADO | RECHAZADO
CONTRATADO / RECHAZADO → (terminales)
```

Se asume además que se puede ir de entrevista a oferta (omitir prueba o referencias), y que prueba técnica y referencias se pueden hacer en cualquier orden, porque RRHH no prohibió atajos ni fijó un orden entre etapas operativas; lo que sí queda prohibido es saltar a un cierre (`CONTRATADO`) sin pasar por `OFERTA`. El proceso es un grafo, no una línea.

**Qué resuelve la arquitectura.** State en su variante de máquina de estados dirigida por tabla, más Template Method y Registry: el grafo vive completo en `ReglasTransicion`; `EstadoBase` fija la validación contra esa tabla para que ningún estado se la salte; `CatalogoEstados` registra automáticamente todos los estados; cada clase de estado solo conserva lo que de verdad cambia según la etapa (su código y, en oferta y contratado, lo que ocurre al entrar). `GestorDeCandidato` y el comando no nombran etapas. Una etapa nueva es una clase nueva más sus filas en la tabla, sin modificar los estados vecinos ni reabrir un `if/else` central como el del `GestorDeCandidato` original. Las pruebas de contrato detectan si la etapa nueva quedó sin conectar.

## 4. Qué ve el candidato en el portal

**Qué no se sabe.** El candidato *“debería ver su propio progreso en el portal, aunque no todos los cambios — hay notas internas que no queremos que vea.”* No se describe el modelo de notas ni qué otros cambios serían invisibles.

**Qué se asumió.** El portal solo muestra el **progreso** del candidato: los eventos de la categoría `EventoDeProgreso` (`EstadoCambiado` y `CambioRevertido`). Las notas internas, si existieran, serían eventos sin esa categoría y por tanto no llegarían a `ActualizarPortalCandidato`.

**Qué resuelve la arquitectura.** El portal es un observador más, no un acoplamiento dentro del cambio de estado. Ampliar o recortar lo visible (ocultar un rechazo interno, mostrar solo el estado vigente, etc.) se decide en ese observador o en qué eventos son de progreso, sin tocar el comando ni el gestor.

## 5. Quién se entera de qué

**Qué está confirmado.** El reclutador se entera de todo. Nómina, solo al confirmar la contratación.

**Qué se asumió.** “Todo” para el reclutador incluye tanto el cambio como el deshacer (necesita saber si se revirtió un rechazo por error). Nómina reacciona únicamente a `CandidatoContratado`; no a una reversión, porque una oferta restaurada no es una alta.

**Qué resuelve la arquitectura.** Cada interesado es un `ObservadorEvento` que declara qué le interesa (`leInteresa`). `GestorDeCandidato` publica los eventos y no conoce destinatarios; `BusEventos` aísla las fallas de cada observador. Agregar o quitar un equipo (por ejemplo, Seguridad o Legal) es agregar o quitar una clase, sin modificar el flujo de cambio de estado.

## 6. Auditoría: quién hizo qué y cuándo

**Qué no se sabe.** RRHH pide saber *quién hizo qué y cuándo* para el error de la semana pasada y para auditoría en general. No pide un módulo de auditoría, ni retención, ni un reporte.

**Qué se asumió.** Basta con que cada evento cargue **autor** y **momento** (`ocurridoEn`), y los cambios de estado además el **estado anterior** y el **nuevo**. Al deshacer, el autor es **quien deshace**, no quien hizo el cambio original. El historial de fotos es la traza reversible; el evento es la traza publicable.

**Qué resuelve la arquitectura.** Esa información viaja en los eventos (`EstadoCambiado`, `CambioRevertido`, `OfertaEmitida`, `CandidatoContratado`). Un auditor futuro puede suscribirse al bus como un observador más, sin cambiar cómo se transiciona.

## 7. Cómo se notifican realmente los equipos

**Qué no se sabe.** El código en producción manda correos con `EmailService` hardcodeado. RRHH habla de “enterarse”, no de canal (correo, Slack, bandeja interna).

**Qué se asumió.** El mecanismo de entrega es intercambiable. Hoy los observadores avisan por `CanalRegistro`, que escribe en el log; mañana puede ser correo o mensajería sin que los observadores ni el gestor lo sepan.

**Qué resuelve la arquitectura.** Strategy: los observadores dependen de `CanalNotificacion`, no de un servicio de correo. Además, `GestorDeCandidato` depende de `PublicarEventos`, no de `BusEventos`. Cambiar el canal, o sustituir el bus en memoria por mensajería externa, no altera el comando, el estado ni el historial.
