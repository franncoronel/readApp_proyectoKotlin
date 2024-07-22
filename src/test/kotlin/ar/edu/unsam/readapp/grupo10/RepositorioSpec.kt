package ar.edu.unsam.readapp.grupo10

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldNotBeIn
import io.kotest.matchers.shouldBe

class RepositorioSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    describe("Dado un repositorio de autores...") {

        val repoDeAutores = Repositorio<Autor>()
        val autor1 = Autor("Gustavo", "Martínez Zuviria", "Hugo Wast")
        val autor2 = Autor("Samuel", "Clemens", "Mark Twain")
        val autor3 = Autor("Roberto", "Christophersen", "Roberto Arlt")
        repoDeAutores.agregar(autor1)
        repoDeAutores.agregar(autor2)

        it("al crear un objeto, este se agrega a la colección y recibe su ID correspondiente") {
            //Act
            repoDeAutores.agregar(autor3)
            //Assert
            repoDeAutores.entidades.shouldContain(autor3)
            autor3.id shouldBe 3
        }

        it("al intentar agregar un objeto ya existente, lanza una excepción") {
            //Assert
            shouldThrow<BusinessException> { repoDeAutores.agregar(autor1) }
        }

        it("al eliminar un objeto, este deja de pertenecer a la colección") {
            //Act
            repoDeAutores.borrar(autor1)
            //Assert
            autor1.shouldNotBeIn(repoDeAutores.entidades)
        }


        it("la búsqueda por ID retorna el objeto solicitado") {
            //Assert
            repoDeAutores.obtenerPorId(2) shouldBe autor2
        }

        it("la busqueda por ID retorna una excepción cuando no hay un objeto con tal id") {
            //Assert
            shouldThrow<BusinessException> { repoDeAutores.obtenerPorId(3) }
        }

        it("la búsqueda por string retorna los elementos que coinciden al menos parcialmente") {
            //Assert
            autor1 shouldBeIn repoDeAutores.buscar("Gus")
        }

        it("la búsqueda por string retorna los elementos que coinciden exactamente") {
            autor2 shouldBeIn repoDeAutores.buscar("Mark Twain")
        }

    }

    describe("Dado un repositorio de usuarios...") {

        val repoDeUsuarios = Repositorio<Usuario>()
        val usuario1 = Usuario("Carlos", "Alvarez", "Chicho")
        val usuario2 = Usuario("Victor", "Blanco", "Viti")
        val usuario3 = Usuario("Adrian", "Martinez", "Maravilla")
        val usuario4 = Usuario("Jose", "Sanchez", "Garrafa")
        repoDeUsuarios.agregar(usuario1)
        repoDeUsuarios.agregar(usuario2)
        repoDeUsuarios.agregar(usuario3)
        repoDeUsuarios.agregar(usuario4)

        it("la búsqueda retorna los elementos que coinciden al menos parcialmente") {
            //Arrange
            //Act
            repoDeUsuarios.buscar("a").shouldContainAll(usuario1, usuario2, usuario3, usuario4)
            //Assert
        }

        it("la busqueda retorna los elementos que coinciden exactamente") {
            usuario2 shouldBeIn repoDeUsuarios.buscar("Viti")
        }
    }

    describe("Dado un repositorio de recomendaciones...") {
        val autor = Autor()
        val libro1 = Libro(autor = autor, titulo = "Rock & Pop: La Imaginación Al Poder")
        val libro2 = Libro(autor = autor, titulo = "Historia del Radioteatro")
        val libro3 = Libro(autor = autor, titulo = "Locución Radiófonica")
        val creadorRecomendacion = Usuario("Mario", "Pergolini").apply {
            agregarLibroLeido(libro1)
            agregarLibroLeido(libro2)
            agregarLibroLeido((libro3))
        }
        val recomendacion = Recomendacion(creador = creadorRecomendacion, detalle = "3 libros sobre radio").apply {
            agregarLibro(libro1, creadorRecomendacion)
            agregarLibro(libro2, creadorRecomendacion)
            agregarLibro(libro3, creadorRecomendacion)
        }
        val repoDeRecomendaciones = Repositorio<Recomendacion>().apply { agregar(recomendacion) }
        it("la búsqueda por string retorna los elementos que coinciden parcialmente") {
            //Arrange
            //Act
            recomendacion shouldBeIn repoDeRecomendaciones.buscar("Pop")
            //Assert
        }
        it("la búsqueda por string retorna los elementos que coinciden exactamente") {
            //Assert
            recomendacion shouldBeIn repoDeRecomendaciones.buscar("Pergolini")
        }
    }

    describe("Dado un repositorio de centros de lectura...") {
        val repoDeCentrosDeLectura = Repositorio<CentroDeLectura>()
        val autorCentroDeLectura = Autor("Ray", "Bradbury")
        val libroCentroDeLectura = Libro(titulo = "Farenheit 451", autor = autorCentroDeLectura)
        val centroDeLectura = Particular(5.0, 10).apply { libroDesignado = libroCentroDeLectura }
        repoDeCentrosDeLectura.agregar(centroDeLectura)
        it("la búsqueda por string retorna los elementos que coinciden exactamente") {
            //Arrange
            //Act
            centroDeLectura shouldBeIn repoDeCentrosDeLectura.buscar("Farenheit 451")
            //Assert
        }
    }

    describe("Dado un repositorio de libros...") {
        val repoDeLibros = Repositorio<Libro>()
        val autoraSaga = Autor("Jota Ka", "Rowling", "J.K.")
        val libroSaga1 = Libro(titulo = "Harry Potter 1", autor = autoraSaga)
        val libroSaga2 = Libro(titulo = "Harry Potter 2", autor = autoraSaga)
        val libroSaga3 = Libro(titulo = "Harry Potter 3", autor = autoraSaga)
        val libroSaga4 = Libro(titulo = "Harry Potter 4", autor = autoraSaga)
        val libroSaga5 = Libro(titulo = "Harry Potter 5", autor = autoraSaga)
        val libroSaga6 = Libro(titulo = "Harry Potter 6", autor = autoraSaga)
        val libroSaga7 = Libro(titulo = "Harry Potter 7", autor = autoraSaga)
        repoDeLibros.agregar(libroSaga1)
        repoDeLibros.agregar(libroSaga2)
        repoDeLibros.agregar(libroSaga3)
        repoDeLibros.agregar(libroSaga4)
        repoDeLibros.agregar(libroSaga5)
        repoDeLibros.agregar(libroSaga6)
        repoDeLibros.agregar(libroSaga7)

        it("la búsqueda por string retorna los elementos que coinciden parcialmente") {
            //Act
            //Assert
            repoDeLibros.buscar("Harry")
                .shouldContainAll(libroSaga1, libroSaga2, libroSaga3, libroSaga4, libroSaga5, libroSaga6, libroSaga7)
            repoDeLibros.buscar("owl")
                .shouldContainAll(libroSaga1, libroSaga2, libroSaga3, libroSaga4, libroSaga5, libroSaga6, libroSaga7)
        }
    }

    describe("Dado un repositorio de libros y un servicio externo...") {

        val autorGenerico = Autor()
        val libro1 = Libro(autor = autorGenerico, titulo = "Harry Potter I")
        val libro2 = Libro(autor = autorGenerico, titulo = "Harry Potter II")
        val libro3 = Libro(autor = autorGenerico, titulo = "Harry Potter III")
        val repositorioLibros = Repositorio<Libro>().apply {
            agregar(libro1)
            agregar(libro2)
            agregar(libro3)
        }
        val stubServicioExterno = StubServicioExterno()
        val actualizador = ActualizadorLibros(stubServicioExterno, repositorioLibros)

        it("se desea actualizar los libros que sufrieron cambios") {
            actualizador.actualizarRepositorio()
            repositorioLibros.obtenerPorId(1).ediciones shouldBe 6
            repositorioLibros.obtenerPorId(1).ventasSemanales shouldBe 15000
            repositorioLibros.obtenerPorId(2).ediciones shouldBe 1
            repositorioLibros.obtenerPorId(2).ventasSemanales shouldBe 1000
            repositorioLibros.obtenerPorId(3).ediciones shouldBe 3
            repositorioLibros.obtenerPorId(3).ventasSemanales shouldBe 11000
        }

        it("los campos que no sufrieron cambios se mantienen iguales luego de la actualizacion") {
            actualizador.actualizarRepositorio()
            repositorioLibros.obtenerPorId(1).titulo shouldBe "Harry Potter I"
            repositorioLibros.obtenerPorId(2).titulo shouldBe "Harry Potter II"
            repositorioLibros.obtenerPorId(3).titulo shouldBe "Harry Potter III"
        }
    }
})

class StubServicioExterno : ServicioDeActualizacion {

    override fun getLibros(): String {
        return """ [{"id": 1,"ediciones": 6,"ventasSemanales": 15000},     
                   {"id": 2,"ediciones": 1,"ventasSemanales": 1000},
                   {"id": 3,"ediciones": 3,"ventasSemanales": 11000}]"""
    }
}
