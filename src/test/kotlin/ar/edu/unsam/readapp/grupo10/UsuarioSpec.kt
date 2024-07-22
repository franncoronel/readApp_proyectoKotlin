package ar.edu.unsam.readapp.grupo10

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class UsuarioSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    val autorVersatil = Autor(lenguaNativa = Lenguaje.ESPANIOL)

    val libroNoDesafiante = Libro(
        paginas = 600,
        autor = autorVersatil
    ).apply {
        this.agregarTraduccion(setOf(Lenguaje.INGLES))
    }
    val libroDesafiante = Libro(autor = autorVersatil).apply {
        this.agregarTraduccion(setOf(Lenguaje.INGLES))
    }
    val libroDesconocido = Libro(autor = autorVersatil)
    val usuarioPromedio = Usuario(
        nombre = "Gabriel",
        apellido = "Tarquini",
        username = "gabitar",
        email = "gabitarquini@unsam.com",
        fechaNacimiento = LocalDate.now().minusYears(27),
        perfil = Precavido,
        tipoLector = Promedio
    )

    usuarioPromedio.agregarLibroLeido(libroNoDesafiante)
    usuarioPromedio.agregarLibroLeido(libroDesafiante)

    val creadorRecomendacionApta = Usuario()
    val creadorRecomendacionNoApta = Usuario()
    val amigo3 = Usuario()

    val recomendacionApta = Recomendacion(creador = creadorRecomendacionApta, detalle = "Mis 2 libros favoritos")
    creadorRecomendacionApta.agregarLibroLeido(libroNoDesafiante)
    creadorRecomendacionApta.agregarLibroLeido(libroDesafiante)
    recomendacionApta.agregarLibro(libroNoDesafiante, creadorRecomendacionApta)
    recomendacionApta.agregarLibro(libroDesafiante, creadorRecomendacionApta)
    val recomendacionNoApta =
        Recomendacion(creador = creadorRecomendacionNoApta, detalle = "1 libro que seguro no leiste")
    creadorRecomendacionNoApta.agregarLibroLeido(libroDesconocido)
    recomendacionNoApta.agregarLibro(libroDesconocido, creadorRecomendacionNoApta)

    describe("Dado un usuario...") {
        usuarioPromedio.agregarAmigo(creadorRecomendacionApta)
        usuarioPromedio.agregarAmigo(creadorRecomendacionNoApta)
        usuarioPromedio.agregarAmigo(amigo3)
        it(" corroborar la edad") {
            usuarioPromedio.edad() shouldBe 27
        }
        it("corroborar el tiempo de lectura promedio con un libro no desafiante") {
            usuarioPromedio.tiempoDeLecturaPromedio(libroNoDesafiante) shouldBe 400.0
        }
        it("corroborar el tiempo de lectura promedio con un libro desafiante") {
            usuarioPromedio.tiempoDeLecturaPromedio(libroDesafiante) shouldBe 800.0
        }
        it("tiene 3 amigos a su lista de amigos") {
            //Act
            //Assert
            usuarioPromedio.amigos.size shouldBe 3
        }
        it("elimina 1 amigo de su lista de amigos") {
            //Act
            usuarioPromedio.eliminarAmigo(creadorRecomendacionNoApta)
            //Assert
            usuarioPromedio.amigos.size shouldBe 2
        }
        it("agrega una recomendacion a valorar") {
            //Act
            usuarioPromedio.agregarRecomendacionAValorar(recomendacionApta)
            //Assert
            usuarioPromedio.recomendacionesAValorar.size shouldBe 1
        }
        it("intenta agregar una valoracion a valorar pero no cumple las condiciones") {
            //Act
            //Assert
            assertThrows<BusinessException> { usuarioPromedio.agregarRecomendacionAValorar(recomendacionNoApta) }
            usuarioPromedio.recomendacionesAValorar.size shouldBe 0
        }
        it("agrega un libro que nunca leyo a su lista de libro por leer") {
            //Act
            usuarioPromedio.agregarLibroALeer(libroDesconocido)
            //Assert
            usuarioPromedio.librosALeer.size shouldBe 1
        }
        it("intenta agregar un libro leido a su lista de libros por leer") {
            //Assert
            assertThrows<BusinessException> { usuarioPromedio.agregarLibroALeer(libroDesafiante) }
            usuarioPromedio.librosALeer.size shouldBe 0
        }
    }

})