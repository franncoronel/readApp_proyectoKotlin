package ar.edu.unsam.readapp.grupo10

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class PerfilUsuarioSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    //SE DEFINIRA UN SOLO USUARIO QUE IRA CAMBIANDO EL TIPO DE LECTOR PARA APROVECHAR ESTA UTILIDAD
    //Y TESTEAR ESTE REQUERIMIENTO SOLICITADO.

    val usuarioPerfil = Usuario(idioma = Lenguaje.INGLES)
    val amigo = Usuario()
    val autorGenerico = Autor()
    val libroPendiente = Libro(autor = autorGenerico)
    val libroLeidoPorAmigo = Libro(autor = autorGenerico)
    val libroNoPendiente = Libro(autor = autorGenerico)
    val libroPoliglota = Libro(autor = autorGenerico).apply {
        agregarTraduccion(setOf(Lenguaje.INGLES, Lenguaje.RUSO, Lenguaje.ALEMAN, Lenguaje.ARABE))
    }
    val libroNoPoliglota = Libro(autor = autorGenerico).apply {
        agregarTraduccion(setOf(Lenguaje.RUSO, Lenguaje.ALEMAN, Lenguaje.ARABE))
    }
    val autorMismoIdiomaUsuario = Autor(lenguaNativa = Lenguaje.INGLES)
    val libroMismoIdioma1 = Libro(autor = autorMismoIdiomaUsuario)
    val libroMismoIdioma2 = Libro(autor = autorMismoIdiomaUsuario).apply {
        agregarTraduccion(setOf(Lenguaje.RUSO, Lenguaje.ALEMAN, Lenguaje.ARABE))
    }

    val autorDistintoIdiomaUsuario = Autor(lenguaNativa = Lenguaje.BENGALI)
    val libroDistintoIdioma = Libro(autor = autorDistintoIdiomaUsuario)

    val autorConsagrado1 = Autor(edad = 50, premios = 1)
    val autorConsagrado2 = Autor(edad = 50, premios = 1)
    val autorNoConsagrado = Autor()
    val libroAutorConsagrado1 = Libro(autor = autorConsagrado1)
    val libroAutorConsagrado2 = Libro(autor = autorConsagrado2)
    val libroAutorNoConsagrado = Libro(autor = autorNoConsagrado)
    val libro10000 = Libro(autor = autorGenerico, palabras = 500000)
    val libro5000 = Libro(autor = autorGenerico, palabras = 250000)

    val creadorRecomendacion = Usuario().apply {
        agregarLibroLeido(libroPendiente)
        agregarLibroLeido(libroLeidoPorAmigo)
        agregarLibroLeido(libroNoPendiente)
        agregarLibroLeido(libroPoliglota)
        agregarLibroLeido(libroNoPoliglota)
        agregarLibroLeido(libroMismoIdioma1)
        agregarLibroLeido(libroMismoIdioma2)
        agregarLibroLeido(libroDistintoIdioma)
        agregarLibroLeido(libroAutorNoConsagrado)
        agregarLibroLeido(libroAutorConsagrado1)
        agregarLibroLeido(libroAutorConsagrado2)
        agregarLibroLeido(libro5000)
        agregarLibroLeido(libro10000)
        agregarAmigo(amigo)
    }

    amigo.agregarLibroLeido(libroLeidoPorAmigo)

    describe("Dado un usuario de perfil lector...") {

        val recomendacionRandom1 =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libroPendiente, usuario = creadorRecomendacion)
            }

        val recomendacionRandom2 =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libroLeidoPorAmigo, usuario = creadorRecomendacion)
            }

        val recomendacionRandom3 =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libroLeidoPorAmigo, usuario = creadorRecomendacion)
                agregarLibro(libro = libroPendiente, usuario = creadorRecomendacion)
            }

        it("se interesa por cualquier recomendacion") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionRandom1) shouldBe true
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionRandom2) shouldBe true
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionRandom3) shouldBe true
        }
    }

    describe("Dado un usuario de perfil precavido...") {

        usuarioPerfil.cambiaPerfil(Precavido)
        usuarioPerfil.agregarLibroALeer(libroPendiente)
        val recomendacionConLibroPendiente =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libroPendiente, usuario = creadorRecomendacion)
            }

        it("se interesa por una recomendacion que tiene un libro pendiente") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionConLibroPendiente) shouldBe true
        }

        val recomendacionConLibroAmigo =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libroLeidoPorAmigo, usuario = creadorRecomendacion)
            }
        usuarioPerfil.agregarAmigo(usuario = amigo)

        it("se interesa por una recomendacion que tiene un libro que leyo un amigo") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionConLibroAmigo) shouldBe true
        }

        val recomendacionLibroAmigoPendiente =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libroLeidoPorAmigo, usuario = creadorRecomendacion)
                agregarLibro(libro = libroPendiente, usuario = creadorRecomendacion)
            }

        it("se interesa por una recomendacion que tiene un libro que leyo un amigo y un libro que tiene pendiente") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionLibroAmigoPendiente) shouldBe true
        }

        val recomendacionNoInteresante =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libroNoPendiente, usuario = creadorRecomendacion)
            }

        it("no se interesa por una recomendacion que no tiene un libro que leyo un amigo ni un libro que tiene pendiente") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionNoInteresante) shouldBe false
        }
    }

    describe("Dado un usuario de perfil poliglota...") {

        usuarioPerfil.cambiaPerfil(Poliglota)
        val recomendacionCon5Idiomas =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libroPoliglota, usuario = creadorRecomendacion)
            }

        it("se interesa por una recomendacion que tiene 5 idiomas distintos") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionCon5Idiomas) shouldBe true
        }

        val recomendacionCon4Idiomas =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libroNoPoliglota, usuario = creadorRecomendacion)
            }

        it("no se interesa por una recomendacion que tiene 4 idiomas distintos") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionCon4Idiomas) shouldBe false
        }
    }

    describe("Dado un usuario de perfil nativista...") {

        usuarioPerfil.cambiaPerfil(Nativista)

        val recomendacionNativa =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libroMismoIdioma1, usuario = creadorRecomendacion)
                agregarLibro(libro = libroMismoIdioma2, usuario = creadorRecomendacion)
            }

        it("se interesa por una recomendacion que tiene todos sus libros con idioma original igual a su idioma nativo (idioma del autor del libro)") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionNativa) shouldBe true
        }

        val recomendacionCasiNativa =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libroMismoIdioma1, usuario = creadorRecomendacion)
                agregarLibro(libro = libroDistintoIdioma, usuario = creadorRecomendacion)
            }

        it("no se interesa por una recomendacion que tiene algunos de sus libros con idioma original igual a su idioma nativo") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionCasiNativa) shouldBe false
        }

        val recomendacionNoNativa =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libroDistintoIdioma, usuario = creadorRecomendacion)
            }

        it("no se interesa por una recomendacion que todos sus libros con idioma original distintos a su idioma nativo") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionNoNativa) shouldBe false
        }
    }

    describe("Dado un usuario de perfil calculador...") {

        usuarioPerfil.cambiaPerfil(Calculador(1600..2400))

        val recomendacionEnRangoMinimo = //1600 minutos para este tipo de usuario
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libroMismoIdioma1, usuario = creadorRecomendacion)
                agregarLibro(libro = libroMismoIdioma2, usuario = creadorRecomendacion)
            }

        it("se interesa por una recomendacion que se encuentra en el valor minimo de su rango de tiempo") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionEnRangoMinimo) shouldBe true
        }

        val recomendacionEnRangoMaximo = //2400 minutos para este tipo de usuario
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libroMismoIdioma1, usuario = creadorRecomendacion)
                agregarLibro(libro = libroMismoIdioma2, usuario = creadorRecomendacion)
                agregarLibro(libro = libroDistintoIdioma, usuario = creadorRecomendacion)
            }

        it("se interesa por una recomendacion que se encuentra en el valor maximo de su rango de tiempo") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionEnRangoMaximo) shouldBe true
        }

        val recomendacionDebajoDelRango =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                this.agregarLibro(libro = libroMismoIdioma1, usuario = creadorRecomendacion)
            }

        it("no se interesa por una recomendacion que se encuentra por debajo del valor minimo de su rango de tiempo") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionDebajoDelRango) shouldBe false
        }

        val recomendacionEncimaDelRango =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                this.agregarLibro(libro = libroMismoIdioma1, usuario = creadorRecomendacion)
                this.agregarLibro(libro = libroMismoIdioma2, usuario = creadorRecomendacion)
                this.agregarLibro(libro = libroDistintoIdioma, usuario = creadorRecomendacion)
                this.agregarLibro(libro = libroPendiente, usuario = creadorRecomendacion)
            }

        it("no se interesa por una recomendacion que se encuentra por encima del valor maximo de su rango de tiempo") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionEncimaDelRango) shouldBe false
        }
    }

    describe("Dado un usuario de perfil demandante...") {

        usuarioPerfil.cambiaPerfil(Demandante)

        val recomendacionValoracion4 =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libroLeidoPorAmigo, usuario = creadorRecomendacion)
            }

        recomendacionValoracion4.agregarOEditarValoracion(usuario = amigo, 4, "Valoro con 4")

        it("se interesa por una recomendacion que tiene una valoracion alta") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionValoracion4) shouldBe true
        }

        val recomendacionValoracion5 =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libroLeidoPorAmigo, usuario = creadorRecomendacion)
            }

        recomendacionValoracion5.agregarOEditarValoracion(amigo, 5, "Valoro con 5")

        it("se interesa por una recomendacion que tiene una valoracion alta") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionValoracion5) shouldBe true
        }

        val recomendacionValoracion3 =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libroLeidoPorAmigo, usuario = creadorRecomendacion)
            }

        recomendacionValoracion3.agregarOEditarValoracion(amigo, 3, "Valoro con 3")

        it("no se interesa por una recomendacion que tiene una valoracion debajo de su requisito") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionValoracion3) shouldBe false
        }
    }

    describe("Dado un usuario de perfil experimentado...") {

        usuarioPerfil.cambiaPerfil(Experimentado)

        val recomendacionConsagrada =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libroAutorNoConsagrado, usuario = creadorRecomendacion)
                agregarLibro(libro = libroAutorConsagrado1, usuario = creadorRecomendacion)
                agregarLibro(libro = libroAutorConsagrado2, usuario = creadorRecomendacion)
            }

        it("se interesa por una recomendacion donde la mayoria de los autores son consagrados") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionConsagrada) shouldBe true
        }

        val recomendacionNoConsagrada =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libroAutorNoConsagrado, usuario = creadorRecomendacion)
                agregarLibro(libro = libroAutorConsagrado1, usuario = creadorRecomendacion)
            }

        it("no se interesa por una recomendacion donde la mayoria de autores no son consagrados") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionNoConsagrada) shouldBe false
        }
    }

    describe("Dado un usuario de perfil cambiante...") {

        usuarioPerfil.cambiaPerfil(Cambiante)
        usuarioPerfil.fechaNacimiento = LocalDate.now().minusYears(25)

        val recomendacionRandom1 =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libroPendiente, usuario = creadorRecomendacion)
            }

        val recomendacionRandom2 =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libroLeidoPorAmigo, usuario = creadorRecomendacion)
            }

        val recomendacionRandom3 =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libroLeidoPorAmigo, usuario = creadorRecomendacion)
                agregarLibro(libro = libroPendiente, usuario = creadorRecomendacion)
            }

        it("dentro del limite de edad se interesa por cualquier recomendacion") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionRandom1) shouldBe true
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionRandom2) shouldBe true
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionRandom3) shouldBe true
        }

        usuarioPerfil.fechaNacimiento = LocalDate.now().minusYears(26)


        val recomendacionEnRango10Mil = //10000 minutos para este tipo de usuario
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libro10000, usuario = creadorRecomendacion)
            }

        it("pasado el límite de edad, se interesa por una recomendacion que se encuentra en el valor minimo de su rango de tiempo") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionEnRango10Mil) shouldBe true
        }

        val recomendacionEnRango15Mil = //15000 minutos para este tipo de usuario
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libro5000, usuario = creadorRecomendacion)
                agregarLibro(libro = libro10000, usuario = creadorRecomendacion)
            }

        it("se interesa por una recomendacion que se encuentra en el valor maximo de su rango de tiempo") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionEnRango15Mil) shouldBe true
        }

        val recomendacionDebajoDelRango =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libro5000, usuario = creadorRecomendacion)
            }

        it("pasado el limite de edad, no se interesa por una recomendacion que se encuentra por debajo del valor minimo de su rango de tiempo") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionDebajoDelRango) shouldBe false
        }

        val recomendacionEncimaDelRango =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                agregarLibro(libro = libro5000, usuario = creadorRecomendacion)
                agregarLibro(libro = libro10000, usuario = creadorRecomendacion)
                agregarLibro(libro = libroPendiente, usuario = creadorRecomendacion)
            }

        it("no se interesa por una recomendacion que se encuentra por encima del valor maximo de su rango de tiempo") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacionEncimaDelRango) shouldBe false
        }
    }

    describe("Dado un usuario multiperfil...") {

        val multiperfil = MultiPerfil().apply {
            agregarPerfil(Leedor)
            agregarPerfil(Poliglota)
            agregarPerfil(Experimentado)
        }

        usuarioPerfil.cambiaPerfil(multiperfil)

        val recomendacion =
            Recomendacion(creador = creadorRecomendacion, detalle = "").apply {
                this.agregarLibro(libro = libroPendiente, usuario = creadorRecomendacion)
            }

        it("se interesa por una recomendacion ya que alguno de sus perfiles encaja con ella") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacion) shouldBe true
        }

        multiperfil.quitarPerfil(Leedor)

        it("no se interesa por una recomendacion ya que ninguno de sus perfiles encaja con ella") {
            usuarioPerfil.buscarRecomendacion(recomendacion = recomendacion) shouldBe false
        }
    }
})