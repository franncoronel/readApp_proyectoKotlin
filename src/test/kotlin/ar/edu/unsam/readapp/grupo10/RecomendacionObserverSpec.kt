package ar.edu.unsam.readapp.grupo10

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.ints.exactly
import io.kotest.matchers.maps.shouldContainValue
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify

class RecomendacionObserverSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    describe("Dada una recomendación...") {

        val creadorRecomendacion = Usuario(nombre = "Nicolas", email = "creador@recomendacion.com")
        val usuario1 = Usuario(nombre = "Carlos")
        val usuario2 = Usuario()
        val libro = Libro(titulo = "Abc", autor = Autor())
        val libro1 = Libro(titulo = "Abcd", autor = Autor())
        val libro2 = Libro(autor = Autor())
        val libro3 = Libro(autor = Autor())
        val libro4 = Libro(autor = Autor())
        val recomendacion = Recomendacion(creadorRecomendacion, detalle = "")
        val recomendacion1 = Recomendacion(creadorRecomendacion, detalle = "")
        val mockedMailSender = mockk<MailSender>(relaxUnitFun = true)

        describe("El Mail observer..") {
            val mailObserver = MailObserver(mockedMailSender)

            it("Un usuario agrega un libro y se envía un mail al creador avisando que un libro fue agregado") {
                //Act
                creadorRecomendacion.apply {
                    agregarAmigo(usuario1)
                    agregarLibroLeido(libro)
                    agregarLibroLeido(libro1)
                }
                usuario1.apply {
                    agregarLibroLeido(libro)
                    agregarLibroLeido(libro1)
                }
                recomendacion1.agregarLibro(libro1, usuario1)
                recomendacion1.agregarObserver(mailObserver)
                recomendacion1.agregarLibro(libro, usuario1)
                //Assert
                verify(exactly = 1) {
                    mockedMailSender.sendMail(
                        Mail(
                            from = "notificaciones@readapp.com.ar",
                            to = "creador@recomendacion.com",
                            subject = "Se agrego un Libro",
                            body = "El usuario: Carlos, agrego el Libro: Abc " + "a la recomendación que tenía estos Títulos: Abcd"
                        )
                    )
                }
            }

            it("El creador agrega un libro y no se envía ningún mail") {
                //Act
                creadorRecomendacion.apply {
                    agregarLibroLeido(libro)
                }
                recomendacion.agregarLibro(libro, creadorRecomendacion)
                //Assert
                verify(exactly = 0) {
                    mockedMailSender.sendMail(
                        mail = Mail(
                            from = "notificaciones@readapp.com.ar",
                            to = "creador@recomendacion.com",
                            subject = "Se agrego un Libro",
                            body = "El usuario: Carlos, agrego el Libro: Abc " + "a la recomendación que tenía estos Títulos: Abc"
                        )
                    )
                }
            }
        }

        describe("El registro observer..") {
            val registroObserver = RegistroObserver()
            recomendacion.agregarObserver(registroObserver)
            it("El creador agrega 2 libros y quedan registrados") {
                //Act
                creadorRecomendacion.apply {
                    agregarLibroLeido(libro)
                    agregarLibroLeido(libro1)
                }
                recomendacion.agregarLibro(libro, creadorRecomendacion)
                recomendacion.agregarLibro(libro1, creadorRecomendacion)
                //Assert
                registroObserver.cantidadLibrosRegistrados() shouldBe 2
            }

            it("Un usuario agrega 3 libros y quedan registrados") {
                //Act
                usuario1.apply {
                    agregarLibroLeido(libro)
                    agregarLibroLeido(libro1)
                    agregarLibroLeido(libro2)
                }
                creadorRecomendacion.apply {
                    agregarAmigo(usuario1)
                    agregarLibroLeido(libro)
                    agregarLibroLeido(libro1)
                    agregarLibroLeido(libro2)

                }
                recomendacion.apply {
                    agregarLibro(libro, usuario1)
                    agregarLibro(libro1, usuario1)
                    agregarLibro(libro2, usuario1)
                }
                //Assert
                registroObserver.cantidadLibrosRegistrados() shouldBe 3
            }

        }

        describe("El limite observer..") {
            var limiteLibrosAgregados = 2
            val usuarioEspecifico = Usuario()
            val limiteObserver = LimiteObserver(usuarioEspecifico, limiteLibrosAgregados)
            recomendacion.agregarObserver(limiteObserver)

            it("Un usuario no especifico agrega libros mas alla del limite y no es eliminado de la lista de amigos del creador") {
                //Act
                usuario2.apply {
                    agregarLibroLeido(libro2)
                    agregarLibroLeido(libro3)
                    agregarLibroLeido(libro4)
                }
                creadorRecomendacion.apply {
                    agregarAmigo(usuario2)
                    agregarLibroLeido(libro2)
                    agregarLibroLeido(libro3)
                    agregarLibroLeido(libro4)
                }
                recomendacion.apply {
                    agregarLibro(libro2, creadorRecomendacion)
                    agregarLibro(libro3, creadorRecomendacion)
                    agregarLibro(libro4, creadorRecomendacion)
                }
                //Assert
                creadorRecomendacion.amigos shouldContain (usuario2)
                creadorRecomendacion.esAmigo(usuario2) shouldBe true
            }

            it("El usuario especifico agrega un libro y queda por debajo del limite, aun es amigo del creador") {
                //Act
                usuarioEspecifico.agregarLibroLeido(libro2)
                creadorRecomendacion.apply {
                    agregarAmigo(usuarioEspecifico)
                    agregarLibroLeido(libro2)
                }
                recomendacion.agregarLibro(libro2, usuarioEspecifico)
                //Assert
                creadorRecomendacion.amigos shouldContain (usuarioEspecifico)
                creadorRecomendacion.esAmigo(usuarioEspecifico) shouldBe true
            }

            it("El usuario especifico agrega un libro alcanzando el limite y es eliminado de la lista de amigos") {
                //Act
                usuarioEspecifico.apply {
                    agregarLibroLeido(libro2)
                    agregarLibroLeido(libro3)
                }
                creadorRecomendacion.apply {
                    agregarAmigo(usuarioEspecifico)
                    agregarLibroLeido(libro2)
                    agregarLibroLeido(libro3)
                }
                recomendacion.agregarLibro(libro2, usuarioEspecifico)
                recomendacion.agregarLibro(libro3, usuarioEspecifico)
                //Assert
                creadorRecomendacion.amigos shouldNotContain (usuarioEspecifico)
                creadorRecomendacion.esAmigo(usuarioEspecifico) shouldBe false
            }

            it("El usuario especifico intenta agregar un nuevo libro a la recomendación pero ya no puede") {
                //
                creadorRecomendacion.apply {
                    agregarAmigo(usuarioEspecifico)
                    agregarLibroLeido(libro2)
                    agregarLibroLeido(libro3)
                    agregarLibroLeido(libro4)
                }
                usuarioEspecifico.apply {
                    agregarLibroLeido(libro2)
                    agregarLibroLeido(libro3)
                    agregarLibroLeido(libro4)
                }
                recomendacion.agregarLibro(libro2, usuarioEspecifico)
                recomendacion.agregarLibro(libro3, usuarioEspecifico)

                //Assert
                creadorRecomendacion.esAmigo(usuarioEspecifico) shouldBe false
                shouldThrow<BusinessException> { recomendacion.agregarLibro(libro4, usuarioEspecifico) }
            }
        }

        describe("El valoración observer") {
            val valoracionObserver = ValoracionObserver()
            recomendacion.agregarObserver(valoracionObserver)

            it("El creador agrega un libro y no se agrega ninguna valoración a la recomendación") {
                creadorRecomendacion.apply {
                    agregarLibroLeido(libro4)
                }
                recomendacion.agregarLibro(libro4, creadorRecomendacion)

                recomendacion.valoraciones.size shouldBe 0
            }
            it("Un usuario que había valorado la recomendación agrega un libro y la valoración que había realizado se mantiene") {
                usuario2.apply {
                    agregarLibroLeido(libro3)
                    agregarLibroLeido(libro4)
                }
                creadorRecomendacion.apply {
                    agregarAmigo(usuario2)
                    agregarLibroLeido(libro3)
                    agregarLibroLeido(libro4)
                }
                recomendacion.agregarLibro(libro3, usuario2)
                recomendacion.agregarOEditarValoracion(usuario = usuario2, valor = 4, comentario = "Genial")
                recomendacion.agregarLibro(libro4, usuario2)

                recomendacion.valoraciones.size shouldBe 1
                recomendacion.valoraciones[usuario2]!!.valor shouldBe 4
                recomendacion.valoraciones[usuario2]!!.comentario shouldBe "Genial"

            }
            it("Un usuario que no había valorado la recomendación agrega un libro y se realiza la valoración predeterminada") {
                usuario2.apply {
                    agregarLibroLeido(libro3)
                }
                creadorRecomendacion.apply {
                    agregarAmigo(usuario2)
                    agregarLibroLeido(libro3)
                }
                recomendacion.agregarLibro(libro3, usuario2)

                recomendacion.valoraciones.size shouldBe 1
                recomendacion.valoraciones[usuario2]!!.valor shouldBe 5
                recomendacion.valoraciones[usuario2]!!.comentario shouldBe "Excelente 100% recomendable"

            }
        }
    }

})