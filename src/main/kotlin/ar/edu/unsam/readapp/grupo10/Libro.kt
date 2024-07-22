package ar.edu.unsam.readapp.grupo10

val LIMITE_PAGINAS: Int = 600

class Libro(
    val palabras: Int = 40000,
    val paginas: Int = 1000,
    var ediciones: Int = 2,
    var ventasSemanales: Int = 10000,
    val lecturaCompleja: Boolean = false,
    val autor: Autor,
    val lenguajeOriginal: Lenguaje = autor.lenguaNativa,
    var titulo: String = ""
) : Entidad {
    var id = 0
    var traducciones = mutableSetOf<Lenguaje>()

    fun idiomas(): MutableSet<Lenguaje> = (traducciones + lenguajeOriginal).toMutableSet()

    fun agregarTraduccion(traduccionesAAgregar: Set<Lenguaje>) {
        traducciones.addAll(traduccionesAAgregar)
    }

    fun borrarTraduccion(traduccionesABorrar: Set<Lenguaje>) {
        traducciones.removeAll(traduccionesABorrar)
    }

    fun esLargo(): Boolean = paginas > LIMITE_PAGINAS

    fun esDesafiante(): Boolean = esLargo() || lecturaCompleja

    fun esBestSeller(): Boolean = (superoVentas() && (superoEdiciones() || muchasTraducciones()))

    fun superoVentas(): Boolean = ventasSemanales >= 10000

    fun superoEdiciones(): Boolean = ediciones > 2

    fun muchasTraducciones(): Boolean = traducciones.size >= 5

    //FUNCIONALIDAD REPOSITORIO

    override fun condicionBusqueda(busqueda: String): Boolean =
        titulo.contains(busqueda) || autor.apellido.contains(busqueda)

    override fun asignarID(identificador: Int) {
        id = identificador
    }

    override fun id(): Int = id

    override fun estaBienConstruido() {
        validaPalabras()
        validaEdiciones()
        validaLecturaCompleja()
        validaAutor()
        autor.estaBienConstruido()
        validaVentasSemanales()
        validaTitulo()
        validaLenguajeOriginal()
    }

    fun validaPalabras() {
        if (palabras <= 0) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: PALABRAS")
    }

    fun validaEdiciones() {
        if (ediciones <= 0) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: EDICIONES")
    }

    fun validaVentasSemanales() {
        if (ventasSemanales <= 0) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: VENTAS SEMANALES")
    }

    fun validaLecturaCompleja() {
        if (lecturaCompleja != null) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: LECTURA COMPLEJA")
    }

    fun validaAutor() {
        if (autor != null) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: AUTOR")
    }

    fun validaLenguajeOriginal() {
        if (lenguajeOriginal == null) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: LENGUAJE")
    }

    fun validaTitulo() {
        if (titulo.isBlank()) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: TITULO")
    }
}