package ar.edu.unsam.readapp.grupo10

class Recomendacion(
    val creador: Usuario,
    var detalle: String,
    var publica: Boolean = true
) : Entidad {
    var id = 0
    val librosRecomendados = mutableSetOf<Libro>()
    val valoraciones = mutableMapOf<Usuario, Valoracion>()
    var recomendacionObservers = mutableSetOf<RecomendacionObserver>()

    fun listaDeAutores(): MutableSet<Autor> = librosRecomendados.map { it.autor }.toMutableSet()

    fun puedeEditar(usuario: Usuario): Boolean = esElCreador(usuario) || (this.amigoPuedeEditar(usuario))

    fun amigoPuedeEditar(usuario: Usuario): Boolean = creador.esAmigo(usuario) && leyoTodosLosLibros(usuario)

    fun esElCreador(usuario: Usuario): Boolean = (usuario == creador)

    fun leyoTodosLosLibros(usuario: Usuario): Boolean =
        usuario.nombresLibrosLeidos().containsAll(librosRecomendados)

    fun agregarLibro(libro: Libro, usuario: Usuario) {
        validarAgregarLibro(libro, usuario)
        librosRecomendados.add(libro)
        recomendacionObservers.forEach { it.notificaLibroAgregado(usuario, libro, this) }
    }

    fun validarAgregarLibro(libro: Libro, usuario: Usuario) {
        if (!puedeEditar(usuario) || !validaLibroParaAgregar(libro, usuario))
            throw BusinessException("No se puede agregar el libro a la recomendacion")
    }

    fun validaLibroParaAgregar(libro: Libro, usuario: Usuario): Boolean =
        validaPorCreador(libro, usuario) || validaPorAmigo(libro, usuario)

    fun validaPorCreador(libro: Libro, usuario: Usuario): Boolean =
        usuario == creador && usuario.leyoElLibro(libro)

    fun validaPorAmigo(libro: Libro, usuario: Usuario): Boolean =
        creador.esAmigo(usuario) && usuario.leyoElLibro(libro) && creador.leyoElLibro(libro)

    fun tiempoLecturaRecomendacionCompleto(usuario: Usuario): Int =
        librosRecomendados.sumOf { usuario.tiempoDeLectura(it) }

    fun tiempoLecturaRecomendacionAhorrado(usuario: Usuario): Int =
        filtrarLibrosLeidos(usuario).sumOf { usuario.tiempoDeLectura(it) }

    fun filtrarLibrosLeidos(usuario: Usuario): List<Libro> =
        librosRecomendados.filter { usuario.leyoElLibro(it) }

    fun tiempoLecturaRecomendacionNeto(usuario: Usuario): Int =
        tiempoLecturaRecomendacionCompleto(usuario) - tiempoLecturaRecomendacionAhorrado(usuario)


    fun puedeValorarPorAutorPreferido(usuario: Usuario): Boolean =
        esUnicoAutor() && autorPreferidoCoincide(usuario)

    fun esUnicoAutor(): Boolean =
        listaDeAutores().size == 1

    fun autorPreferidoCoincide(usuario: Usuario): Boolean =
        listaDeAutores().all { usuario.esAutorPreferido(it) }

    fun agregarOEditarValoracion(usuario: Usuario, valor: Int, comentario: String) {
        validarAgregarOEditar(usuario, valor)
        valoraciones[usuario] = Valoracion(valor, comentario)
    }

    fun validarAgregarOEditar(usuario: Usuario, valor: Int) {
        if (!validarValoracion(usuario) || !validarValor(valor))
            throw BusinessException("No cumple las condiciones para valorar una recomendación")
    }

    fun validarValoracion(usuario: Usuario): Boolean =
        (!esElCreador(usuario) && (leyoTodosLosLibros(usuario) || puedeValorarPorAutorPreferido(usuario)))

    fun validarValor(valor: Int): Boolean = (valor in 1..5)

    fun contieneLibro(libro: Libro): Boolean = librosRecomendados.contains(libro)

    fun idiomas(): Set<Lenguaje> =
        (librosRecomendados.flatMap { libro -> libro.idiomas() }).toSet()

    fun cantidadDeIdiomas(): Int = idiomas().size

    fun esConsagrada(): Boolean {
        val consagrados = listaDeAutores().count { it.esConsagrado() }
        return consagrados > listaDeAutores().size / 2
    }

    fun valoracionPromedio(): Double =
        ((valoraciones.values.sumOf { it -> it.valor }) / valoraciones.size).toDouble()

    fun valoradaPor(usuario: Usuario): Boolean = valoraciones.keys.contains(usuario)

    override fun asignarID(identificador: Int) {
        id = identificador
    }

    override fun id(): Int = id

    override fun condicionBusqueda(busqueda: String): Boolean =
        creador.apellido == busqueda || coincidenciaParcial(busqueda)

    fun coincidenciaParcial(busqueda: String): Boolean =
        detalle.contains(busqueda) || librosRecomendados.any { it.titulo.contains(busqueda) }

    override fun estaBienConstruido() {
        validaCreador()
        creador.estaBienConstruido()
        validaDetalle()
        validaPublica()

    }

    fun validaCreador() {
        if (creador == null) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: CREADOR")
    }

    fun validaDetalle() {
        if (detalle.isBlank()) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: DETALLE")
    }

    fun validaPublica() {
        if (publica == null) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: PUBLICA/PRIVADA")
    }


    fun agregarObserver(nuevoObserver: RecomendacionObserver): Boolean = recomendacionObservers.add(nuevoObserver)

    fun eliminarObserver(observer: RecomendacionObserver): Boolean = recomendacionObservers.remove(observer)

    fun estaInactivo(usuario: Usuario) =
        !esElCreador(usuario) && !valoradaPor(usuario)

    fun titulos(): MutableList<String> = librosRecomendados.map { it.titulo }.toMutableList()

}

data class Valoracion(var valor: Int, var comentario: String)