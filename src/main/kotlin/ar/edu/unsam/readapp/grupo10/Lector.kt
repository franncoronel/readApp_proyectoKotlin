package ar.edu.unsam.readapp.grupo10

interface Lector {
    fun calculoPorLector(libro: Libro, usuario: Usuario): Double
}

object Promedio : Lector {
    override fun calculoPorLector(libro: Libro, usuario: Usuario): Double = 1.0
}

object Ansioso : Lector {
    override fun calculoPorLector(libro: Libro, usuario: Usuario): Double = if (libro.esBestSeller()) 0.5 else 0.8
}

object Fanatico : Lector {
    override fun calculoPorLector(libro: Libro, usuario: Usuario): Double =
        if (usuario.esAutorPreferido(libro.autor) && !usuario.leyoElLibro(libro))
            calculoFanatico(libro, usuario)
        else
            1.0

    fun calculoFanatico(libro: Libro, usuario: Usuario): Double =
        if (!libro.esLargo())
            2.0 * libro.paginas / usuario.tiempoDeLecturaPromedio(libro)
        else
            (2.0 * LIMITE_PAGINAS + (libro.paginas - LIMITE_PAGINAS)) / usuario.tiempoDeLecturaPromedio(libro)

}

object Recurrente : Lector {
    override fun calculoPorLector(libro: Libro, usuario: Usuario): Double =
        if (!usuario.hayLibrosLeidos())
            throw BusinessException("No ha leido ningun libro anterioremente, no es un lector recurrente.")
        else
            calculoPorRecurrente(libro, usuario)


    fun calculoPorRecurrente(libro: Libro, usuario: Usuario): Double =
        if (loLeyoMasDeUnaVez(libro, usuario))
            factorLectura(libro, usuario)
        else
            1.0

    fun factorLectura(libro: Libro, usuario: Usuario): Double =
        maxOf(0.95, 1.0 - (usuario.vecesLeido(libro) / 100.0))

    fun loLeyoMasDeUnaVez(libro: Libro, usuario: Usuario) = usuario.vecesLeido(libro) > 1
}