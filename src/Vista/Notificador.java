package vista;

/*
    Pequeña interfaz para avisar mensajes al usuario (la barra de estado de abajo).

    ¿Por qué una interfaz? Así los paneles (PanelEventos, PanelZonas, etc.) no
    dependen de la clase concreta de la ventana, solo de este contrato. Es la idea
    de "programar contra una interfaz, no contra una implementación" (principio de
    Inversión de Dependencias de SOLID). Si mañana cambiamos cómo se muestran los
    mensajes, los paneles no se enteran.
*/
public interface Notificador
{
    void informar(String mensaje);
}
