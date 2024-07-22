package ar.edu.unsam.readapp.grupo10

abstract class ProcesoAdministracion(val mailSender: MailSender, var tipoProceso: String) {

    fun ejecutar() {
        ejecutarProceso()
        notificarProceso()
    }

    fun notificarProceso() {
        mailSender.sendMail(
            Mail(
                from = "",
                to = "admin@readapp.com.ar",
                subject = "Se realizó el proceso: $tipoProceso.",
                body = "Se realizó el proceso: $tipoProceso."
            )
        )
    }

    abstract fun ejecutarProceso()
}


class ActualizarLibros(
    mailSender: MailSender,
    val repositorio: Repositorio<Libro>,
    service: ServicioDeActualizacion,
    tipoProceso: String = "actualización de repositorio de libros"
) : ProcesoAdministracion(mailSender, tipoProceso) {
    val actualizador = ActualizadorLibros(service, repositorio)
    override fun ejecutarProceso() {
        actualizador.actualizarRepositorio()
    }
}


class AgregarAutores(
    mailSender: MailSender,
    val repositorio: Repositorio<Autor>,
    val listaDeAutores: List<Autor>,
    tipoProceso: String = "agregar autores"
) : ProcesoAdministracion(mailSender, tipoProceso) {
    override fun ejecutarProceso() {
        listaDeAutores.forEach { repositorio.agregar(it) }
    }
}

class BorrarUsuariosInactivos(
    mailSender: MailSender,
    val repoUsuarios: Repositorio<Usuario>,
    val repoRecomendaciones: Repositorio<Recomendacion>,
    tipoProceso: String = "eliminar usuarios inactivos"
) : ProcesoAdministracion(mailSender, tipoProceso) {
    override fun ejecutarProceso() {
        usuariosInactivos().forEach { repoUsuarios.borrar(it) }
    }

    fun usuariosInactivos(): Set<Usuario> =
        condicionPorRepo().intersect(condicionPorAmigo())

    fun condicionPorRepo(): Set<Usuario> =
        repoUsuarios.entidades.filter { usuario ->
            repoRecomendaciones.entidades.all { it.estaInactivo(usuario) }
        }.toSet()


    fun condicionPorAmigo(): Set<Usuario> =
        repoUsuarios.entidades.filter { usuario ->
            repoUsuarios.entidades.all { !it.esAmigo(usuario) }
        }.toSet()
}


class BorrarCentrosExpirados(
    mailSender: MailSender,
    val repositorio: Repositorio<CentroDeLectura>,
    tipoProceso: String = "borrar centros de lectura expirados"
) : ProcesoAdministracion(mailSender, tipoProceso) {
    override fun ejecutarProceso() {
        filtrarExpirados().forEach { repositorio.borrar(it) }
    }

    fun filtrarExpirados(): List<CentroDeLectura> = repositorio.entidades.filter { it.expiroPublicacion() }
}

class Administrador() {
    fun administrar(procesos: List<ProcesoAdministracion>) {
        procesos.forEach { procesoAdministracion -> procesoAdministracion.ejecutar() }
    }
}