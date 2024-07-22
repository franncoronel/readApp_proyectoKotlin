package ar.edu.unsam.readapp.grupo10

import java.time.LocalDate
import java.time.temporal.ChronoUnit

class Usuario(
    val nombre: String = "",
    val apellido: String = "",
    val username: String = "",
    val email: String = "",
    val idioma: Lenguaje = Lenguaje.ESPANIOL,
    var fechaNacimiento: LocalDate = LocalDate.now().minusYears(20),
    val palabrasPorMinuto: Int = 100,
    var perfil: Perfil = Leedor,
    var tipoLector: Lector = Promedio
) : Entidad {
    var id = 0
    val recomendacionesAValorar = mutableSetOf<Recomendacion>()
    val amigos = mutableListOf<Usuario>()
    val librosLeidos = mutableMapOf<Libro, Int>()
    val librosALeer = mutableSetOf<Libro>()
    val autoresPreferidos = mutableSetOf<Autor>()

// METODOS GENERALES DEL USUARIO

    fun edad(): Int = ChronoUnit.YEARS.between(fechaNacimiento, LocalDate.now()).toInt()

    fun tiempoDeLectura(libro: Libro): Int =
        (tiempoDeLecturaPromedio(libro) * tipoLector.calculoPorLector(libro, this)).toInt()

    fun tiempoDeLecturaPromedio(libro: Libro): Int = calculaTiempoDeLecturaPromedio(libro) * factorDesafiante(libro)

    fun calculaTiempoDeLecturaPromedio(libro: Libro): Int = libro.palabras / palabrasPorMinuto

    fun factorDesafiante(libro: Libro): Int = if (libro.esDesafiante()) 2 else 1

    fun agregarAmigo(usuario: Usuario) = amigos.add(usuario)

    fun eliminarAmigo(usuario: Usuario) = amigos.remove(usuario)

    fun esAmigo(usuario: Usuario): Boolean = amigos.contains(usuario)

    fun esAutorPreferido(autor: Autor): Boolean = autoresPreferidos.contains(autor)

    fun hayLibrosLeidos(): Boolean = librosLeidos.isNotEmpty()

    fun leyoElLibro(libro: Libro): Boolean = librosLeidos.containsKey(libro)

    fun agregarLibroLeido(libro: Libro) {
        librosLeidos[libro] = vecesLeido(libro) + 1
    }

    fun nombresLibrosLeidos(): MutableSet<Libro> = librosLeidos.keys

    fun vecesLeido(libro: Libro) = librosLeidos.getOrDefault(libro, 0)

    fun agregarLibroALeer(libro: Libro) {
        if (leyoElLibro(libro))
            throw BusinessException("No se puede agregar un libro leído a la lista de libros a leer")
        librosALeer.add(libro)
    }

    fun agregarAutorPreferido(autor: Autor) = autoresPreferidos.add(autor)

// METODOS USUARIO<->RECOMENDACION

    fun buscarRecomendacion(recomendacion: Recomendacion): Boolean =
        perfil.recomendacionAdecuada(recomendacion, this)

    fun crearOEditarValoracion(recomendacion: Recomendacion, valor: Int, comentario: String) {
        recomendacion.agregarOEditarValoracion(this, valor, comentario)
    }

    fun agregarRecomendacionAValorar(recomendacion: Recomendacion) {
        if (!recomendacion.validarValoracion(this))
            throw BusinessException("No cumple las condiciones para agregar una valoracion")
        recomendacionesAValorar.add(recomendacion)
    }

// METODOS USUARIO<->PERFIL_USUARIO

    fun cambiaPerfil(nuevoPerfil: Perfil) {
        perfil = nuevoPerfil
    }

    fun librosLeidosAmigos(): MutableSet<Libro> {
        val librosLeidosPorAmigos = mutableSetOf<Libro>()
        amigos.forEach { amigo -> librosLeidosPorAmigos.addAll(amigo.librosLeidos.keys) }
        return librosLeidosPorAmigos
    }

    fun loLeyoUnAmigo(recomendacion: Recomendacion): Boolean =
        librosLeidosAmigos().any { recomendacion.contieneLibro(it) }

    fun tieneUnLibroPendiente(recomendacion: Recomendacion): Boolean =
        librosALeer.any { recomendacion.contieneLibro(it) }
// METODOS USUARIO <-> TIPO_LECTOR

    fun cambiaTipoLector(nuevoTipoLector: Lector) {
        tipoLector = nuevoTipoLector
    }
// METODOS USUARIO <-> Centro de lectura

    fun solicitarReserva(centroDeLectura: CentroDeLectura) {
        centroDeLectura.registrarReserva(this)
    }

//FUNCIONALIDAD REPOSITORIO

    override fun condicionBusqueda(busqueda: String): Boolean =
        nombre.contains(busqueda) || apellido.contains(busqueda) || username == busqueda

    override fun asignarID(identificador: Int) {
        id = identificador
    }

    override fun id(): Int = id

    override fun estaBienConstruido() {
        validarNombre()
        validarApellido()
        validarUsername()
        validarEmail()
        validarIdioma()
        validarFechaNacimiento()
        validarPalabrasPorMinuto()
        validarPerfil()
        validarTipoLector()
    }

    fun validarNombre() {
        if (nombre.isBlank()) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: NOMBRE")
    }

    fun validarApellido() {
        if (apellido.isBlank()) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: APELLIDO")
    }

    fun validarUsername() {
        if (username.isBlank()) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: USERNAME")
    }

    fun validarEmail() {
        if (email.isBlank()) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: EMAIL")
    }

    fun validarIdioma() {
        if (idioma == null) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: IDIOMA")
    }

    fun validarFechaNacimiento() {
        if (fechaNacimiento == null) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: FECHA DE NACIMIENTO")
    }

    fun validarPalabrasPorMinuto() {
        if ((palabrasPorMinuto < 0) || (palabrasPorMinuto == null)) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: PALABRAS POR MINUTO")
    }

    fun validarPerfil() {
        if (perfil == null) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: PERFIL")
    }

    fun validarTipoLector() {
        if (tipoLector == null) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: TIPO DE LECTOR")
    }
}