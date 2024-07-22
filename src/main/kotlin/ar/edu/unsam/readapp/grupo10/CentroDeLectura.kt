package ar.edu.unsam.readapp.grupo10

import java.time.LocalDate
import kotlin.math.roundToInt

const val COSTO_APP_RESERVAS = 1000.0

abstract class CentroDeLectura() : Entidad {
    lateinit var direccion: String
    lateinit var libroDesignado: Libro
    var duracion: Double = 0.0
    var id = 0
    var fechas = mutableSetOf<LocalDate>()
    val participantes = mutableSetOf<Usuario>()

    fun agregarFecha(fecha: LocalDate) {
        fechas.add(fecha)
    }

    fun cantidadFechas() = fechas.size

    abstract fun participantesMaximos(): Int

    fun cantidadParticipantes() = participantes.size

    fun registrarReserva(usuario: Usuario) {
        validarReserva(usuario)
        participantes.add(usuario)
    }

    fun validarReserva(usuario: Usuario) {
        if (!hayLugar())
            throw BusinessException("No puede anotarse a este centro de lectura.")
    }

    fun expiroPublicacion(): Boolean = vencieronFechas() || !hayLugar()

    fun vencieronFechas(): Boolean = fechas.all { it.isBefore(LocalDate.now()) }

    fun hayLugar(): Boolean = cantidadParticipantes() < participantesMaximos()

    fun costoDeReserva(): Double = COSTO_APP_RESERVAS + adicional()

    abstract fun adicional(): Double

    override fun condicionBusqueda(busqueda: String): Boolean =
        libroDesignado.titulo.contains(busqueda)

    override fun asignarID(identificador: Int) {
        id = identificador
    }

    override fun id(): Int = id

    override fun estaBienConstruido() {
        validaDireccion()
        validaLibroDesignado()
        libroDesignado.estaBienConstruido()
        validaDuracion()
        validaFechas()
        validacionParticular()
    }

    fun validaDireccion() {
        if (direccion.isBlank()) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: DIRECCIÓN")
    }

    fun validaLibroDesignado() {
        if (libroDesignado == null) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: LIBRO DESIGNADO")
    }

    fun validaDuracion() {
        if (duracion <= 0) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: DURACIÓN DEL ENCUENTRO")
    }

    fun validaFechas() {
        if (fechas.isEmpty()) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: FECHAS")
    }

    abstract fun validacionParticular()
}


const val ADICIONAL_PARTICULAR = 500.0

class Particular(
    val porcentajeASuperar: Double,
    val maximoParticipantes: Int
) : CentroDeLectura() {

    override fun participantesMaximos(): Int = maximoParticipantes

    override fun adicional(): Double = if (superoPorcentaje()) ADICIONAL_PARTICULAR else 0.0

    fun superoPorcentaje() = cantidadParticipantes() > (participantesMaximos() * porcentajeASuperar)

    override fun validacionParticular() {
        validaPorcentaje()
        validaParticipantes()
    }

    fun validaPorcentaje() {
        if (porcentajeASuperar < 0.0) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: PORCENTAJE A SUPERAR")
    }

    fun validaParticipantes() {
        if (maximoParticipantes <= 0) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: PARTICIPANTES MAXIMOS")
    }

}

const val ADICIONAL_EDITORIAL = 800.0

class Editorial(
    var montoAAlcanzar: Int,
    var asisteElAutor: Boolean
) : CentroDeLectura() {

    override fun participantesMaximos(): Int = (montoAAlcanzar / costoDeReserva()).roundToInt()

    override fun adicional() = ADICIONAL_EDITORIAL + adicionalAutor()

    fun adicionalAutor(): Double =
        if (asisteElAutor)
            adicionalBestSeller()
        else
            0.0

    fun adicionalBestSeller(): Double {
        return if (!libroDesignado.esBestSeller())
            200.0
        else libroDesignado.ventasSemanales * 0.1
    }

    override fun validacionParticular() {
        validaMonto()
        validaAsisteAutor()
    }

    fun validaMonto() {
        if (montoAAlcanzar < 0.0) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: MONTO A SUPERAR")
    }

    fun validaAsisteAutor() {
        if (asisteElAutor == null) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: ASISTE EL AUTOR")
    }

}

class Biblioteca(
    val metrosCuadrados: Int,
    val gastosFijos: Double
) : CentroDeLectura() {

    override fun participantesMaximos(): Int = metrosCuadrados

    override fun adicional(): Double = (gastosFijos * factorMargen()) / 100

    fun factorMargen(): Double {
        return if (fechas.size < 5)
            1 + (fechas.size / 10.0)
        else 1.5
    }

    override fun validacionParticular() {
        validaMetrosCuadrados()
        validaGastosFijos()
    }

    fun validaMetrosCuadrados() {
        if (metrosCuadrados < 0) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: METROS CUADRADOS")
    }

    fun validaGastosFijos() {
        if (gastosFijos < 0.0) throw BusinessException("ERROR DE CONSTRUCCIÓN EN: GASTOS FIJOS")
    }

}
