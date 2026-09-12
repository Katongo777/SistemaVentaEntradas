# Sistema de Gestión de Eventos y Venta de Tickets

## Descripción

Este proyecto es un programa en Java construido bajo el patrón de arquitectura **MVC (Modelo-Vista-Controlador)**. Permite gestionar un catálogo de eventos (Charlas y Seminarios) y procesar la venta de tickets aplicando, descuentos y generación automática de identificadores seguros.

## Características Principales

* **Gestión de Eventos:** Creación de `Charlas` y `Seminarios` utilizando herencia y polimorfismo, con generación automática de códigos.
* **Control de Aforo y Zonas:** Cada evento maneja colecciones de ubicaciones, controlando el stock y evitando sobreventas.
* **Venta de Tickets Segura:** Asignación de UUIDs únicos por ticket para evitar enumeración y vulnerabilidades.
* **Lógica de Descuentos:** Aplicación de descuentos automáticos por perfil (ej. tercera edad) y soporte para cupones promocionales mediante sobrecarga de métodos.
* **Persistencia de Datos:** Guardado y carga automática del historial mediante un manejador de archivos CSV.
* **Manejo de Excepciones:** Respuestas ante errores de usuario (edad insuficiente, falta de stock) sin interrumpir la ejecución del programa.

## Requisitos del Sistema

* Java Development Kit (JDK) 8 o superior.
* IDE recomendado: Apache NetBeans (opcional para visualización de código).

## Estructura del Proyecto
El código fuente está dividido estrictamente en tres paquetes principales:

* **modelo:** Contiene las clases: `Evento`, `Charla`, `Seminario`, `Ticket`, `Usuario`, `Ubicacion`.
* **vista:** Contiene las interacciones del programa con el usuario (`VistaConsola`, `CreadorEventos`, `VendedorTickets`, `EditorEventos`).
* **controlador:** Contiene la lógica del negocio (`GestorVentas`, `ManejadorArchivos`).
* **excepciones:** Excepciones personalizadas de para stock y edad.

## Cómo Compilar y Ejecutar

### Opción 1: Desde una Terminal / Consola (Recomendado)

1. Abre tu terminal y navega hasta la carpeta raíz del proyecto (donde se encuentra la carpeta `src`).
2. Compila todos los archivos `.java` ejecutando el siguiente comando:
   ```bash
   javac -d bin src/**/*.java
   ```

3. Ejecuta la clase principal iniciando la aplicación:

    ```bash
    java -cp bin main.Principal
    ```

### Opción 2: Desde NetBeans

1. Abre NetBeans y selecciona File > Open Project.

2. Busca y selecciona la carpeta de este proyecto.

3. Haz clic derecho sobre el proyecto en la pestaña "Projects" y selecciona Run, o presiona F6.

### Uso del Sistema

Al iniciar el sistema consultará si deseas ingresar en "Modo Consola" o "Modo Ventana". Selecciona la opción ```1``` (Modo Consola) para acceder al menú principal interactivo. El sistema leerá automáticamente el archivo ```eventos.csv``` para cargar el estado anterior; si no existe, se creará uno nuevo al cerrar la aplicación usando la opción ```0```.