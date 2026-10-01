package modelo;

/*
    Lista fija de temáticas/categorías que usa el sistema.

    La dejamos en un solo lugar para que tanto la creación de eventos (temática)
    como la recomendación (interés del usuario) usen exactamente las mismas
    opciones. Así no pasa que uno escriba "tecnologia" y otro "Tecnología" y no
    coincidan: al elegir de una lista, siempre calzan (idea de no repetir, DRY).
*/
public final class Categorias
{
    private Categorias() { }

    public static final String[] TEMATICAS = {
        "Tecnología",
        "Negocios",
        "Ciencia",
        "Arte",
        "Salud",
        "Educación",
        "Música",
        "Deporte"
    };
}
