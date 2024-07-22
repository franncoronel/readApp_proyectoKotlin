package ar.edu.unsam.readapp.grupo10

interface RecomendacionObserver {
    fun notificaLibroAgregado(usuario: Usuario, libro: Libro, recomendacion: Recomendacion)
}

class MailObserver(val mailSender: MailSender) : RecomendacionObserver {

    override fun notificaLibroAgregado(usuario: Usuario, libro: Libro, recomendacion: Recomendacion) {
        if (!recomendacion.esElCreador(usuario))
            enviarMail(usuario, libro, recomendacion)
    }

    fun enviarMail(usuario: Usuario, libro: Libro, recomendacion: Recomendacion) {
        val titulosRecomendados = recomendacion.titulos()
        titulosRecomendados.remove(libro.titulo)
        val titulosRecomendadosString = titulosRecomendados.joinToString(", ")
        mailSender.sendMail(
            Mail(
                from = "notificaciones@readapp.com.ar",
                to = recomendacion.creador.email,
                subject = "Se agrego un Libro",
                body = "El usuario: ${usuario.nombre}, agrego el Libro: ${libro.titulo} " +
                        "a la recomendación que tenía estos Títulos: $titulosRecomendadosString"
            )
        )
    }
}

interface MailSender {
    fun sendMail(mail: Mail)
}

data class Mail(val from: String, val to: String, val subject: String, val body: String)

class RegistroObserver() : RecomendacionObserver {
    val registroDeUsuarios: MutableMap<Usuario, MutableList<Libro>> = mutableMapOf()

    override fun notificaLibroAgregado(usuario: Usuario, libro: Libro, recomendacion: Recomendacion) {
        if (!usuarioRegistrado(usuario)) {
            registroDeUsuarios[usuario] = mutableListOf<Libro>()
        }
        val librosUsuario = registroDeUsuarios[usuario]!!
        librosUsuario.add(libro)
    }

    fun cantidadLibrosRegistrados() = registroDeUsuarios.values.sumOf { it.size }

    fun usuarioRegistrado(usuario: Usuario) = registroDeUsuarios.containsKey(usuario)

}

class LimiteObserver(val usuarioEspecifico: Usuario, val limite: Int) : RecomendacionObserver {
    var librosAgregados = 0

    override fun notificaLibroAgregado(usuario: Usuario, libro: Libro, recomendacion: Recomendacion) {
        librosAgregados += 1
        chequeaLimite(recomendacion)
    }

    fun chequeaLimite(recomendacion: Recomendacion) {
        if (librosAgregados == limite)
            recomendacion.creador.eliminarAmigo(usuarioEspecifico)
    }
}

class ValoracionObserver() : RecomendacionObserver {
    override fun notificaLibroAgregado(usuario: Usuario, libro: Libro, recomendacion: Recomendacion) {
        if (noEsElCreadorNoValoro(usuario, recomendacion)) {
            recomendacion.agregarOEditarValoracion(usuario, 5, "Excelente 100% recomendable")
            //recomendacion.valoraciones[usuario] = Valoracion(5,"Excelente 100% recomendable")
        }
    }

    fun noEsElCreadorNoValoro(usuario: Usuario, recomendacion: Recomendacion) =
        !recomendacion.esElCreador(usuario) && !recomendacion.valoradaPor(usuario)
}