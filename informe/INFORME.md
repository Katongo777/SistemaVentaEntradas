# Informe Proyecto I - Sistema de Información (SIA)
## Sistema de gestión de venta de entradas a eventos

**Asignatura:** INF2236 - Programación Avanzada

**Profesor:** Claudio Cubillos

**Tema (N° 9):** Venta de entradas a eventos


**Integrantes:**
- Renato Agustín Bermúdez Salfate
- Iván Octavio García Briones
- Nicolás Daniel Torres González

**Repositorio GitHub:** https://github.com/Katongo777/SistemaVentaEntradas

---

## Introducción

Nuestro proyecto es un sistema para gestionar la venta de entradas (tickets) a
eventos. La idea nació porque cuando uno quiere ir a una charla o a un seminario
normalmente hay que llevar un control de cuántos cupos quedan, en qué zona te
sientas y cuánto pagas según tu perfil (por ejemplo, si eres adulto mayor tienes
descuento). Quisimos modelar ese problema real usando lo que fuimos aprendiendo
en el curso: clases, herencia, colecciones, excepciones, etc.

El sistema permite crear eventos (Charlas y Seminarios), agregarles distintas
zonas con su capacidad y precio, registrar usuarios que compran, vender tickets
controlando el stock y la edad, y guardar el catálogo de eventos, zonas y cupones en un archivo CSV para
recuperarlo en la siguiente ejecución. El historial de ventas y el número de
asientos vendidos se mantienen en memoria durante la ejecución.

El programa está organizado con el patrón **Modelo-Vista-Controlador (MVC)** y se
puede usar tanto por **consola** como por **ventana** (interfaz gráfica).

A continuación respondemos punto por punto cada requerimiento, explicando cómo lo
aplicamos y dónde está la evidencia en el código.

---

## SIA-1 - Análisis de los datos y funcionalidades

**Datos que maneja el sistema:**

- **Eventos:** tienen un código (ID), un nombre y una temática. Hay dos tipos:
  *Charlas* (con un expositor principal) y *Seminarios* (con una duración en días).
- **Zonas / Ubicaciones:** cada evento puede tener varias zonas (por ejemplo
  General, VIP). Cada zona tiene un nombre, una capacidad máxima, cuántos asientos
  ya se vendieron y un precio base.
- **Usuarios:** la persona que compra. En memoria se maneja nombre, rut, edad y área de
  interés. El área de interés se utiliza en las recomendaciones.
- **Tickets:** la entrada que se genera al vender. Guarda el comprador, el evento,
  la zona y el costo final que se pagó. El código del ticket se genera a partir de los primeros ocho caracteres de
  un UUID. Esto reduce la probabilidad de colisiones, aunque no garantiza
  unicidad absoluta.

**Principales funcionalidades:**

1. Crear, listar, buscar, editar y eliminar eventos.
2. Agregar, listar, buscar, editar y eliminar zonas dentro de un evento.
3. Vender tickets, controlando que haya stock y que el comprador cumpla la edad
   mínima cuando corresponde. Se aplican descuentos (adulto mayor y cupón).
4. Listar los tickets vendidos (historial de ventas).
5. Recomendar eventos a un usuario según su área de interés y su edad.
6. Guardar y cargar los datos en un archivo CSV.

Todo esto le da sentido al proyecto porque cubre el ciclo completo de un negocio
sencillo de venta de entradas: catálogo, inventario (cupos), venta y persistencia.

---

## SIA-2 - Diseño conceptual UML del Dominio y su código en Java

El diagrama de clases del dominio se incluye en este informe y está disponible
en el Anexo A al final del PDF, en `informe/uml/uml-dominio.png` y en formato vectorial
`informe/uml/uml-dominio.svg`. Resume las clases y las operaciones
principales; no pretende enumerar todos los getters y setters.

![Diagrama de clases del dominio](uml/uml-dominio.png)

**Explicación del diseño:**

- `Evento` es una **clase abstracta** con lo común a todos los eventos. De ella
  heredan `Charla` y `Seminario`.
- Cada `Evento` contiene una lista de `Ubicacion`, modelada como composición.
  Las zonas se gestionan dentro del evento y forman la colección anidada.
  Eliminar el evento del catálogo no elimina los objetos referenciados por
  tickets vendidos durante esa ejecución.
- `Ticket` conecta a un `Usuario`, un `Evento` y una `Ubicacion`.
- `GestorVentas` (controlador) guarda el catálogo de eventos en un `HashMap` y el
  historial de tickets en un `ArrayList`.

Las clases del dominio están en el paquete `modelo` y `GestorVentas`
pertenece al paquete `controlador`. Las clases del modelo son:
`Evento.java`, `Charla.java`, `Seminario.java`, `Ubicacion.java`, `Usuario.java`,
`Ticket.java`.

---

## SIA-3 - Buenas prácticas

- **Atributos privados con getters y setters:** todas las clases del modelo tienen
  sus atributos `private` y sus métodos de lectura/escritura. Por ejemplo, en
  `Ubicacion.java` los atributos `nombreZona`, `capacidadMaxima`, etc. son privados
  y tienen su `get`/`set`.
- **Código modularizado:** usamos el patrón MVC, separando el proyecto en paquetes
  `modelo` (los datos), `vista` (lo que ve e ingresa el usuario), `controlador`
  (la lógica) y `excepciones`. Además, en la vista de consola cada tarea grande
  está en su propia clase (`CreadorEventos`, `CreadorTickets`, `EditorEventos`,
  `AdministradorZonas`), en vez de tener todo en un solo archivo gigante.
- **Documentación interna:** cada clase tiene un comentario que explica para qué
  sirve, y los métodos importantes están comentados.
- **Datos iniciales:** al iniciar, si no existe el archivo `eventos.csv`, se cargan
  dos eventos de ejemplo (una Charla y un Seminario, cada uno con su zona). Esto
  está en el método `cargarEventos()` de `ManejadorArchivos.java`, y permite probar
  cualquier funcionalidad de inmediato sin tener que crear datos a mano.

Una buena práctica adicional que aplicamos: los métodos que devuelven las
colecciones (`getZonas()`, `getMapaEventos()`, `getHistorialVentas()`) retornan una
**vista inmodificable** (`Collections.unmodifiableList` / `unmodifiableMap`) para impedir que desde afuera se agreguen o eliminen elementos directamente
en las colecciones. Los objetos contenidos siguen siendo mutables por sus
métodos y setters.

Además, en la interfaz gráfica por pestañas separamos cada tema en su propia clase
(un panel por responsabilidad) y usamos una interfaz `Notificador` para desacoplar
los paneles de la ventana. Con eso aplicamos principios de **SOLID** (responsabilidad
única, inversión de dependencias) y **GRASP** (controlador, alta cohesión, bajo
acoplamiento, experto en información, polimorfismo). Está explicado con más detalle
en `informe/diseno-interfaz.md`.

---

## SIA-4 - Dos colecciones (una anidada, al menos un mapa, del JCF)

Usamos dos colecciones, ambas del **Java Collections Framework**:

1. **Colección principal (un mapa):** `HashMap<String, Evento>` en `GestorVentas.java`
   (atributo `eventosMa`). La llave es el código del evento y el valor es el evento.
   Elegimos un `HashMap` porque casi siempre buscamos un evento por su código (ID), y
   con el mapa esa búsqueda es directa (`buscarEvento` hace `eventosMa.get(codigo)`)
   en vez de tener que recorrer una lista completa.

2. **Colección anidada:** `ArrayList<Ubicacion>` dentro de cada `Evento`
   (atributo `zonas` en `Evento.java`). Cada evento tiene su propia lista de zonas.
   Usamos un `ArrayList` porque las zonas simplemente se recorren y no necesitan una
   llave.

Así se cumple que la segunda colección está anidada (una lista de zonas *dentro de*
cada elemento del mapa) y que al menos una es un mapa.

Además, como funcionalidad extra, cada `Evento` tiene también un
`Map<String, Double>` con sus **cupones de descuento** propios (código → porcentaje).
Es otra colección anidada del JCF que usamos para los descuentos.

---

## SIA-5 - Dos clases con sobrecarga de métodos

La sobrecarga es tener el mismo nombre de método pero con distinta lista de
parámetros. La usamos en dos clases (ambas se usan en el programa):

1. **`Ticket.java`** → `calcularCostoFinal()` y `calcularCostoFinal(String codigoDescuento)`.
   La primera calcula el costo con el descuento de adulto mayor; la segunda hace lo
   mismo pero además aplica un cupón si el código es válido **para ese evento**
   (cada evento tiene sus propios cupones).

2. **`GestorVentas.java`** → `venderTicket(Usuario, Evento, Ubicacion)` y
   `venderTicket(Usuario, Evento, Ubicacion, String cupon)`. La segunda versión
   recibe un cupón extra. Según si el usuario ingresa cupón o no, la vista llama a
   una u otra (se ve en `CreadorTickets.java`).

---

## SIA-6 - Dos clases con sobreescritura de métodos

La sobreescritura es cuando una subclase reemplaza un método del padre. La clase
abstracta `Evento` define el método abstracto `mostrarDetalles()`, y las dos
subclases lo sobreescriben con `@Override`:

1. **`Charla.java`** → `mostrarDetalles()` muestra el expositor.
2. **`Seminario.java`** → `mostrarDetalles()` muestra la duración en días.

Ambas se usan cuando listamos eventos (por polimorfismo, cada evento muestra sus
propios detalles).

---

## SIA-7 - Menú: insertar y listar, para ambas colecciones por separado

En el menú de consola (`VistaConsola.java`) hay opciones separadas:

- **Eventos (colección principal):** opción 1 *Agregar evento*, opción 2 *Listar eventos*.
- **Zonas (colección anidada):** opción 6 *Agregar zona a un evento*, opción 7
  *Listar zonas de un evento*.

Así, insertar y listar existe para las dos colecciones, cada una en su propia opción.
En la interfaz gráfica también están estos botones por separado.

---

## SIA-8 - Menú: editar, eliminar y buscar, para ambas colecciones por separado

- **Eventos:** opción 3 *Buscar evento*, opción 4 *Editar evento*, opción 5
  *Eliminar evento* (clases `VistaConsola` y `EditorEventos`).
- **Zonas:** opción 8 *Buscar zona*, opción 9 *Editar zona*, opción 10 *Eliminar
  zona* (clase `AdministradorZonas`, métodos `buscarZona()`, `editarZona()`,
  `eliminarZona()`).

Para poder buscar y eliminar una zona sin romper el encapsulamiento, agregamos en
`Evento.java` los métodos `buscarUbicacion(nombre)` y `eliminarUbicacion(nombre)`,
que trabajan sobre la lista interna sin exponerla.

---

## SIA-9 - Funcionalidad propia con subconjunto filtrado

Implementamos una **recomendación de eventos**: el sistema pide el nombre, la edad
y el área de interés de un usuario, y le muestra solo los eventos que le podrían
gustar.

El filtro está en `GestorVentas.eventosSugeridos(Usuario)`. El criterio es:

- La temática del evento coincide con el área de interés del usuario (comparamos
  sin distinguir mayúsculas ni tildes, para que "Tecnología" y "tecnologia" cuenten
  como iguales - eso lo hace el método `normalizar()`).
- Si el usuario es menor de 18 años, se descartan los Seminarios (porque en la venta
  esos eventos exigen ser mayor de edad).

El resultado es un **subconjunto filtrado** del catálogo, distinto de una simple
inserción/edición/listado. En consola es la opción 13 (*Recomendar eventos*) y en la
ventana la pestaña *Recomendar*.

Al crear eventos y solicitar recomendaciones, la temática o el interés se
eligen de una **lista fija** de categorías (clase `Categorias`). La edición de
la temática por consola sigue permitiendo texto libre; el filtro normaliza
mayúsculas y tildes y admite coincidencias parciales.

Como funcionalidad adicional, cada evento tiene sus propios **cupones de descuento**
que se pueden agregar, listar y eliminar (opciones 14-16 en consola, y la pestaña
*Cupones* en la ventana). Al vender, el cupón ingresado se valida contra los cupones
de ese evento.

---

## SIA-10 - Funcionalidades por consola y por ventana

Al iniciar el programa (`Principal.java`) se le pregunta al usuario si quiere usar
**Modo Consola (1)** o **Modo Ventana (2)**.

- **Consola:** menú de texto con todas las opciones (`VistaConsola.java`).
- **Ventana:** interfaz gráfica hecha con **Swing**, organizada en **pestañas**
  (Eventos, Zonas, Cupones, Ventas, Recomendar), con formularios y tablas para los
  listados (`VistaVentanaPro.java` y sus paneles). Usa el mismo controlador
  `GestorVentas`, así que consola y ventana comparten la misma lógica.

Las dos vistas ofrecen la gestión de eventos, zonas, cupones, ventas y
recomendaciones. La explicación del diseño está en `informe/diseno-interfaz.md`.
Como evidencia visual se pueden adjuntar capturas reales de las pestañas
Eventos, Zonas, Cupones, Ventas y Recomendar.

---

## SIA-11 - Persistencia de datos con sistema batch

Usamos un archivo **CSV** (`eventos.csv`) manejado por `ManejadorArchivos.java`:

- **Al iniciar:** `cargarEventos()` lee el archivo y reconstruye los eventos con sus
  zonas y sus cupones. Si el archivo no existe, carga datos iniciales por defecto.
- **Al salir:** `guardarEventosBatch()` escribe todos los eventos (con sus zonas
  anidadas y sus cupones empaquetados) de vuelta al CSV. Esto se llama al elegir
  "Guardar y salir" en la consola, o al cerrar la ventana en el modo gráfico.

Es un esquema batch: se carga todo al comienzo y se graba todo al final.

**Alcance de la persistencia:** el CSV guarda el catálogo con el nombre,
capacidad y precio de las zonas y los cupones de cada evento. En esta versión
no guarda usuarios, tickets ni el contador de asientos vendidos. Al reiniciar,
el historial de ventas está vacío y los contadores de ocupación vuelven a cero.
Por eso la persistencia implementada corresponde al catálogo, no a un registro
completo y permanente de las ventas.

---

## SIA-12 - Dos excepciones propias manejadas con try-catch

Creamos dos excepciones en el paquete `excepciones`:

1. **`StockAgotadoException`** → se lanza cuando se intenta vender un ticket para
   una zona que ya no tiene cupos.
2. **`EdadInsuficienteException`** → se lanza cuando un menor de edad intenta comprar
   para un evento que exige mayoría de edad (Seminario).

Ambas se lanzan en `GestorVentas.venderTicket(...)` y se atrapan con `try-catch` en
la vista (`CreadorTickets.java` en consola y `PanelVentas.java` en la ventana),
mostrando un mensaje claro en vez de que el programa se caiga.

Además, para el problema que nos habían observado (que el programa se caía al
escribir texto donde iba un número), agregamos bloques `try-catch` con
`NumberFormatException` en todas las entradas numéricas (capacidad, precio, edad,
duración), de modo que si el usuario escribe letras, la consola vuelve a pedir el dato
y la interfaz gráfica muestra un mensaje para corregirlo.

---

## SIA-13 - Uso de GitHub (mínimo 6 commits)

El proyecto está versionado en GitHub y tiene más de 6 commits, hechos a medida que
fuimos construyendo las clases del modelo, el controlador, la vista, la persistencia
y las correcciones.

**Repositorio:** https://github.com/Katongo777/SistemaVentaEntradas

---

## Requerimientos opcionales

- **SIA-O4 (MVC):** sí lo implementamos. El proyecto está separado en `modelo`,
  `vista` y `controlador` (más `excepciones`). Las vistas capturan y validan entradas y muestran resultados. La venta y
  la recomendación se delegan a `GestorVentas`, las operaciones de zonas y
  cupones usan métodos de `Evento` y la persistencia está en
  `ManejadorArchivos`. Esta distribución evita duplicar el cálculo de precios
  y las reglas de stock y edad en ambas vistas.

Los otros opcionales (gráfico estadístico, planilla de cálculo, Javadoc) no se
implementaron en esta entrega.

---

## Instrucciones de instalación y ejecución

**Requisitos:** Java JDK 8 o posterior. El proyecto de NetBeans está
configurado con nivel de fuente y destino Java 1.8. Las fuentes utilizan
características y APIs compatibles con Java 8.

**Opción 1 - NetBeans:**
1. Abrir NetBeans → *File > Open Project* y seleccionar la carpeta del proyecto.
2. Ejecutar con *Run* (F6).

**Opción 2 - IntelliJ IDEA:**
1. Abrir la carpeta del proyecto y marcar `src` como carpeta de fuentes.
2. Seleccionar un JDK y compilar con *Build > Build Project*.
3. Ejecutar el método `main` de `main.Principal`.

**Opción 3 - PowerShell en Windows:**
Desde la raíz del proyecto:
```powershell
$archivos = Get-ChildItem src -Recurse -Filter *.java
javac -encoding UTF-8 -d bin $archivos.FullName
java -cp bin main.Principal
```

Al iniciar, elegir 1 (consola) o 2 (ventana). Los datos se guardan en `eventos.csv`
al salir.

---

## Conclusión

Con este proyecto pudimos aplicar de forma concreta los contenidos del curso:
herencia y polimorfismo (Evento/Charla/Seminario), encapsulamiento, colecciones del
JCF (un mapa y una lista anidada), sobrecarga y sobreescritura, manejo de
excepciones, persistencia en archivo y separación en capas con MVC, todo con dos
formas de uso (consola y ventana). La integración se realizó por partes, comprobando la compilación de cada
etapa. La persistencia conserva eventos, zonas y cupones; ampliar el guardado
al historial de ventas y a los asientos ocupados queda como mejora pendiente.
