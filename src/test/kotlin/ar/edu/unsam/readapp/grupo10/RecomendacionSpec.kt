package ar.edu.unsam.readapp.grupo10

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class RecomendacionSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    describe("Test recomendación") {
        val autorGenerico = Autor()
        val libro1 = Libro(autor = autorGenerico)
        val libro2 = Libro(autor = autorGenerico)
        val libro3 = Libro(autor = autorGenerico)
        val libro4 = Libro(autor = autorGenerico)

        val amigoDelCreador = Usuario(
            nombre = "Matías",
            apellido = "Martin",
            username = "mamartin",
            email = "martinmati@gmail.com",
            idioma = Lenguaje.ESPANIOL,
            fechaNacimiento = LocalDate.now().minusYears(55),
            palabrasPorMinuto = 800,
            perfil = Demandante,
            tipoLector = Ansioso
        ).apply {
            this.agregarLibroLeido(libro1)
            this.agregarLibroLeido(libro2)
            this.agregarLibroLeido(libro3)
        }

        val creadorRecomendacion = Usuario(
            nombre = "Pablo",
            apellido = "Lopez",
            username = "pablopez",
            email = "lopezp@gmail.com",
            idioma = Lenguaje.ESPANIOL,
            fechaNacimiento = LocalDate.now().minusYears(45),
            palabrasPorMinuto = 600,
            perfil = Precavido,
            tipoLector = Promedio
        ).apply {
            this.agregarLibroLeido(libro1)
            this.agregarLibroLeido(libro2)
            this.agregarAmigo(amigoDelCreador)
        }

        val usuarioAutorPreferido = Usuario(
            nombre = "Jorge",
            apellido = "Schmidt",
            username = "jorgito",
            email = "jorgesch@gmail.com",
            idioma = Lenguaje.ESPANIOL,
            fechaNacimiento = LocalDate.now().minusYears(62),
            palabrasPorMinuto = 300,
            perfil = Experimentado,
            tipoLector = Fanatico
        ).apply {
            this.agregarAutorPreferido(autorGenerico)
            this.agregarLibroLeido(libro2)
            this.agregarLibroLeido(libro4)
        }
        val usuarioPrejuicioso = Usuario()

        describe("Dada una recomendación pública") {
            //Arrange
            val recomendacionPublica =
                Recomendacion(
                    creador = creadorRecomendacion,
                    detalle = "2 libros de programación que debes leer"
                ).apply {
                    agregarLibro(libro1, creador)
                    agregarLibro(libro2, creador)
                }
            it("El creador debe poder agregar únicamente libros que haya leído") {
                //Act
                //Assert
                recomendacionPublica.librosRecomendados.size shouldBe 2
                assertThrows<BusinessException> {
                    recomendacionPublica.agregarLibro(libro3, creadorRecomendacion)
                }
            }
            describe("Dado un amigo del creador") {
                it("Debería poder agregar libros que leyeron él y el creador si leyó todos los libros de la recomendación.") {
                    //Act
                    //Assert
                    assertThrows<BusinessException> {
                        recomendacionPublica.agregarLibro(libro3, amigoDelCreador)
                    }
                }
                it("Se debe poder saber cuánto tiempo le tomará leer la recomendación.") {
                    //Act
                    //Assert
                    recomendacionPublica.tiempoLecturaRecomendacionCompleto(amigoDelCreador) shouldBe 160
                    recomendacionPublica.tiempoLecturaRecomendacionAhorrado(amigoDelCreador) shouldBe 160
                    recomendacionPublica.tiempoLecturaRecomendacionNeto(amigoDelCreador) shouldBe 0
                }
                it("Debe poder dejar una valoración.") {
                    //Act
                    amigoDelCreador.crearOEditarValoracion(
                        recomendacion = recomendacionPublica,
                        valor = 3,
                        comentario = "Muy enriquecedora."
                    )
                    //Assert
                    recomendacionPublica.valoraciones.size shouldBe 1
                    recomendacionPublica.valoradaPor(amigoDelCreador) shouldBe true
                }

                it("Si intenta dejar una valoracion fuera de rango, falla.") {
                    //Assert
                    assertThrows<BusinessException> {
                        amigoDelCreador.crearOEditarValoracion(
                            recomendacion = recomendacionPublica,
                            valor = 6,
                            comentario = "Muy enriquecedora."
                        )
                    }
                    recomendacionPublica.valoraciones.size shouldBe 0
                    recomendacionPublica.valoradaPor(amigoDelCreador) shouldBe false
                }
            }
            describe("Dado un usuario fanático") {
                it("Debería poder dejar una valoración porque todos los libros pertenecen a uno de sus autores preferidos.") {
                    //Act
                    usuarioAutorPreferido.crearOEditarValoracion(
                        recomendacion = recomendacionPublica,
                        valor = 5,
                        comentario = "Gran autor!!!"
                    )
                    //Assert
                    recomendacionPublica.listaDeAutores().size shouldBe 1
                    usuarioAutorPreferido.autoresPreferidos.contains(autorGenerico) shouldBe true
                    recomendacionPublica.valoraciones.size shouldBe 1
                    recomendacionPublica.valoradaPor(usuarioAutorPreferido) shouldBe true
                }
                it("Se debe poder saber el tiempo que le tomará y el que ahorrará al leer la recomendación.") {
                    //Act
                    //Assert
                    recomendacionPublica.tiempoLecturaRecomendacionCompleto(usuarioAutorPreferido) shouldBe 1866
                    recomendacionPublica.tiempoLecturaRecomendacionAhorrado(usuarioAutorPreferido) shouldBe 266
                    recomendacionPublica.tiempoLecturaRecomendacionNeto(usuarioAutorPreferido) shouldBe 1600
                }
                it("No puede editar la recomendación porque no es amigo del creador") {
                    //Assert
                    assertThrows<BusinessException> {
                        recomendacionPublica.agregarLibro(libro4, usuarioAutorPreferido)
                    }
                }
            }
            describe("Dado un usuario...") {
                it("que no leyó ninguno de los libros de la recomendación, no puede dejar una valoración") {
                    //Assert
                    assertThrows<BusinessException> {
                        usuarioPrejuicioso.crearOEditarValoracion(
                            recomendacion = recomendacionPublica,
                            valor = 3,
                            comentario = "Se ve interesante"
                        )
                    }
                }
            }
        }
    }
})