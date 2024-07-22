package ar.edu.unsam.readapp.grupo10

interface Perfil {
    fun recomendacionAdecuada(recomendacion: Recomendacion, usuario: Usuario): Boolean = true
}

object Leedor : Perfil {}

object Precavido : Perfil {
    override fun recomendacionAdecuada(recomendacion: Recomendacion, usuario: Usuario): Boolean =
        usuario.loLeyoUnAmigo(recomendacion) || usuario.tieneUnLibroPendiente(recomendacion)
}

object Poliglota : Perfil {
    override fun recomendacionAdecuada(recomendacion: Recomendacion, usuario: Usuario): Boolean =
        recomendacion.cantidadDeIdiomas() >= 5
}

object Nativista : Perfil {
    override fun recomendacionAdecuada(recomendacion: Recomendacion, usuario: Usuario): Boolean =
        recomendacion.librosRecomendados.all { it.autor.coincideConUsuario(usuario) }
}

class Calculador(val rango: ClosedRange<Int>) : Perfil {
    override fun recomendacionAdecuada(recomendacion: Recomendacion, usuario: Usuario): Boolean =
        recomendacion.tiempoLecturaRecomendacionCompleto(usuario) in rango
}

object Demandante : Perfil {
    override fun recomendacionAdecuada(recomendacion: Recomendacion, usuario: Usuario): Boolean =
        recomendacion.valoracionPromedio() in 4.0..5.0
}

object Experimentado : Perfil {
    override fun recomendacionAdecuada(recomendacion: Recomendacion, usuario: Usuario): Boolean =
        recomendacion.esConsagrada()
}

object Cambiante : Perfil {
    override fun recomendacionAdecuada(recomendacion: Recomendacion, usuario: Usuario): Boolean =
        perfilActual(usuario).recomendacionAdecuada(recomendacion, usuario)

    fun perfilActual(usuario: Usuario): Perfil =
        if (usuario.edad() > 25) {
            Calculador(10000..15000)
        } else
            Leedor
}

class MultiPerfil : Perfil {
    val perfiles: MutableSet<Perfil> = mutableSetOf()

    fun agregarPerfil(perfil: Perfil) = perfiles.add(perfil)

    fun quitarPerfil(perfil: Perfil) = perfiles.remove(perfil)

    override fun recomendacionAdecuada(recomendacion: Recomendacion, usuario: Usuario): Boolean {
        return perfiles.any { perfil -> perfil.recomendacionAdecuada(recomendacion, usuario) }
    }
}