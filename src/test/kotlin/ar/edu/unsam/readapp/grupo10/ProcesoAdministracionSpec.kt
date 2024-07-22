package ar.edu.unsam.readapp.grupo10

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.*
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate

class ProcesoAdministracionSpec : DescribeSpec({
    isolationMode = InstancePerTest

    val mockedMailSender = mockk<MailSender>(relaxUnitFun = true)
    val administrador = Administrador()

    describe("Dada una operación de eliminación de usuarios inactivos...") {
        val usuarioInactivo1 = Usuario()
        val usuarioActivoPorAmistad = Usuario() //Es amigo del UA1
        val usuarioActivoCreadorRecomendacion =
            Usuario().apply { agregarAmigo(usuarioActivoPorAmistad) } //Creador de una recomendación
        val recomendacion1 =
            Recomendacion(creador = usuarioActivoCreadorRecomendacion, detalle = "Librazos", publica = true)
        val recomendacion2 = Recomendacion(
            creador = usuarioActivoCreadorRecomendacion,
            detalle = "Estos libros no están tan buenos",
            publica = true
        )
        val usuarioActivoPorValorar = Usuario().apply {
            this.crearOEditarValoracion(
                recomendacion = recomendacion1,
                valor = 4,
                comentario = "Me gustó"
            )
        } //Evaluó la recomendación de UA1
        val repoDeUsuarios = Repositorio<Usuario>().apply {
            agregar(usuarioActivoCreadorRecomendacion)
            agregar(usuarioActivoPorValorar)
            agregar(usuarioActivoPorAmistad)
            agregar(usuarioInactivo1)
        }
        val repoDeRecomendaciones = Repositorio<Recomendacion>().apply {
            agregar(recomendacion1)
            agregar(recomendacion2)
        }
        val eliminarUsuariosInactivos = BorrarUsuariosInactivos(mockedMailSender, repoDeUsuarios, repoDeRecomendaciones)
        it("... el administrador puede borrar a un usuario inactivo") {
            //Arrange
            //Act
            administrador.administrar(listOf(eliminarUsuariosInactivos))
            //Assert
//            usuarioActivoCreadorRecomendacion.recomendaciones.size shouldBe 2
            repoDeUsuarios.entidades.shouldContainAll(
                usuarioActivoCreadorRecomendacion,
                usuarioActivoPorValorar,
                usuarioActivoPorAmistad
            )
            usuarioInactivo1.shouldNotBeIn(repoDeUsuarios.entidades)
        }
        it("... el administrador intenta borrar usuario inactivos sin que los haya y nada ocurre") {
            //Arrange
            repoDeUsuarios.borrar(usuarioInactivo1)
            val repoActivos = repoDeUsuarios
            //Act
            administrador.administrar(listOf(eliminarUsuariosInactivos))
            //Assert
            repoActivos.entidades shouldBeEqual repoDeUsuarios.entidades
        }
        it("...al borrar a un usuario inactivo, se envía el correo correspondiente a la casilla de administración") {
            //Arrange
            //Act
            administrador.administrar(listOf(eliminarUsuariosInactivos))
            //Assert
            usuarioInactivo1.shouldNotBeIn(repoDeUsuarios.entidades)
            verify(exactly = 1) {
                mockedMailSender.sendMail(
                    mail = Mail(
                        from = "",
                        to = "admin@readapp.com.ar",
                        subject = "Se realizó el proceso: eliminar usuarios inactivos.",
                        body = "Se realizó el proceso: eliminar usuarios inactivos."
                    )
                )
            }
        }
    }
    describe("Dada una operación de eliminación de centros de lectura expirados") {
        val centroDeLecturaExpirado = Editorial(asisteElAutor = false, montoAAlcanzar = 18000).apply {
            agregarFecha(LocalDate.now().minusDays(3))
            agregarFecha(LocalDate.now().minusDays(10))
            agregarFecha(LocalDate.now().minusDays(12))
        }
        val centroDeLecturaActivo = Biblioteca(gastosFijos = 10.0, metrosCuadrados = 3).apply {
            agregarFecha(LocalDate.now().plusDays(3))
            agregarFecha(LocalDate.now().plusDays(10))
            agregarFecha(LocalDate.now().plusDays(12))
        }
//        val usuarioCDL1 = Usuario().apply { solicitarReserva(centroDeLecturaActivo) }
        val repoDeCDL = Repositorio<CentroDeLectura>().apply {
            agregar(centroDeLecturaActivo)
            agregar(centroDeLecturaExpirado)
        }
        val eliminarExpirados = BorrarCentrosExpirados(mockedMailSender, repoDeCDL)
        it("...puede eliminar un centro de lectura expirado") {
            //Arrange
            //Act
            administrador.administrar(listOf(eliminarExpirados))
            //Assert
            centroDeLecturaActivo.shouldBeIn(repoDeCDL.entidades)
            centroDeLecturaExpirado.shouldNotBeIn(repoDeCDL.entidades)
        }
        it("...no elimina centros de lectura si ninguno expiró") {
            //Arrange
            repoDeCDL.borrar(centroDeLecturaExpirado)
            //Act
            administrador.administrar(listOf(eliminarExpirados))
            //Assert
            repoDeCDL.entidades.size shouldBe 1
        }
        it("...al eliminar un centro de lectura expirado, se envía el correo correspondiente a la casilla de administración") {
            //Arrange
            //Act
            administrador.administrar(listOf(eliminarExpirados))
            //Assert
            centroDeLecturaExpirado.shouldNotBeIn(repoDeCDL.entidades)
            verify(exactly = 1) {
                mockedMailSender.sendMail(
                    mail = Mail(
                        from = "",
                        to = "admin@readapp.com.ar",
                        subject = "Se realizó el proceso: borrar centros de lectura expirados.",
                        body = "Se realizó el proceso: borrar centros de lectura expirados."
                    )
                )
            }
        }
    }
    describe("Dada una operación de agregación de autores de forma masiva") {
        val autor1 = Autor()
        val autor2 = Autor()
        val autor3 = Autor()
        val autor4 = Autor()
        val autor5 = Autor()
        val repoDeAutores = Repositorio<Autor>().apply {
            agregar(autor3)
            agregar(autor4)
            agregar(autor5)
        }
        val listaDeAutores1 = listOf(autor1, autor2)
        val listaDeAutores2 = listOf(autor4, autor5)
        val agregarAutores = AgregarAutores(mockedMailSender, repoDeAutores, listaDeAutores1)
        val agregarAutoresYaExistentes = AgregarAutores(mockedMailSender, repoDeAutores, listaDeAutores2)
        it("...puede agregar autores de forma masiva") {
            //Arrange
            //Act
            administrador.administrar(listOf(agregarAutores))
            //Assert
            repoDeAutores.entidades.shouldContainAll(autor1, autor2)
            repoDeAutores.entidades.size shouldBe 5
        }
        it("...intentar agregar autores que ya forman parte del repositorio arroja una excepción") {
            //Arrange
//            agregarAutores.apply{
//                listaDeAutores = listaDeAutores2      //Quizás listaDeAutores podría ser val... así podrian ser más dinámicas las instancias del proceso de administración, pero no sé qué dice la teoría al respecto
//            }
            //Act
            //Assert
            shouldThrow<BusinessException> { administrador.administrar(listOf(agregarAutoresYaExistentes)) }
        }
        it("...al agregar autores de forma masiva a un repositorio, se envía el correo correspondiente a la casilla de administración") {
            //Arrange
            //Act
            administrador.administrar(listOf(agregarAutores))
            //Assert
            repoDeAutores.entidades.size shouldBe 5
            verify(exactly = 1) {
                mockedMailSender.sendMail(
                    mail = Mail(
                        from = "",
                        to = "admin@readapp.com.ar",
                        subject = "Se realizó el proceso: agregar autores.",
                        body = "Se realizó el proceso: agregar autores."
                    )
                )
            }
        }
    }
    describe("Dada una operación de actualización de repositorio de libros") {
        val autorGenerico = Autor()
        val libro1 = Libro(autor = autorGenerico, titulo = "Design Patterns", ediciones = 3, ventasSemanales = 20)
        val libro2 = Libro(autor = autorGenerico, titulo = "Martin Fierro", ediciones = 2, ventasSemanales = 100)
        val libro3 = Libro(autor = autorGenerico, titulo = "La Biblia", ediciones = 1, ventasSemanales = 1000)
        val repoDeLibros = Repositorio<Libro>().apply {
            agregar(libro1)
            agregar(libro2)
            agregar(libro3)
        }
        val stubServicioExterno = StubServicioExterno()
        val actualizarRepoLibros = ActualizarLibros(mockedMailSender, repoDeLibros, stubServicioExterno)
        it("...puede actualizar un repositorio de libros correctamente") {
            //Arrange
            //Act
            administrador.administrar(listOf(actualizarRepoLibros))
            //Assert
            repoDeLibros.obtenerPorId(1).ediciones shouldBe 6
            repoDeLibros.obtenerPorId(1).ventasSemanales shouldBe 15000
            repoDeLibros.obtenerPorId(2).ediciones shouldBe 1
            repoDeLibros.obtenerPorId(2).ventasSemanales shouldBe 1000
            repoDeLibros.obtenerPorId(3).ediciones shouldBe 3
            repoDeLibros.obtenerPorId(3).ventasSemanales shouldBe 11000
        }
        it("...los campos que no forman parte del libroJson no reciben modificaciones") {
            //Arrange
            //Act
            administrador.administrar(listOf(actualizarRepoLibros))
            //Assert
            repoDeLibros.obtenerPorId(1).titulo shouldBe "Design Patterns"
            repoDeLibros.obtenerPorId(2).titulo shouldBe "Martin Fierro"
            repoDeLibros.obtenerPorId(3).titulo shouldBe "La Biblia"
        }
        it("...al actualizar un repositorio de libros, se envía el correo correspondiente a la casilla de administración") {
            //Arrange
            //Act
            administrador.administrar(listOf(actualizarRepoLibros))
            //Assert
            verify(exactly = 1) {
                mockedMailSender.sendMail(
                    mail = Mail(
                        from = "",
                        to = "admin@readapp.com.ar",
                        subject = "Se realizó el proceso: actualización de repositorio de libros.",
                        body = "Se realizó el proceso: actualización de repositorio de libros."
                    )
                )
            }
        }
    }
})
