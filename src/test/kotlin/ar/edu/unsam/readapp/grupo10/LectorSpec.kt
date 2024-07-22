package ar.edu.unsam.readapp.grupo10

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class LectorSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest
    //SE DEFINIRA UN SOLO USUARIO QUE IRA CAMBIANDO EL TIPO DE LECTOR PARA APROVECHAR ESTA UTILIDAD
    //Y TESTEAR ESTE REQUERIMIENTO SOLICITADO.

    val usuarioMultiLector = Usuario()
    val autorTesteo = Autor()
    val libroTesteo = Libro(autor = autorTesteo)

    describe("Dado un usuario lector promedio...") {
        it("lee un libro y el tiempo de lectura es el mismo que el tiempo de lectura promedio") {
            //Act
            usuarioMultiLector.cambiaTipoLector(Promedio)
            usuarioMultiLector.agregarLibroLeido(libroTesteo)
            //Arrange
            usuarioMultiLector.tiempoDeLectura(libroTesteo) shouldBe 800
        }
    }

    describe("Dado un usuario lector ansioso...") {
        usuarioMultiLector.cambiaTipoLector(Ansioso)
        val libroNoBestSeller = Libro(autor = autorTesteo)
        val libroBestSeller = Libro(autor = autorTesteo, ediciones = 3)

        it("lee un libro que no es best seller y el tiempo de lectura promedio se ve disminuído") {
            usuarioMultiLector.agregarLibroLeido(libroNoBestSeller)
            usuarioMultiLector.tiempoDeLectura(libroNoBestSeller) shouldBe 640
        }
        it("lee un libro best seller y el tiempo de lectura promedio se ve disminuído acorde al requerimiento") {
            usuarioMultiLector.agregarLibroLeido(libroBestSeller)
            usuarioMultiLector.tiempoDeLectura(libroBestSeller) shouldBe 400
        }
    }

    describe("Dado un usuario lector fanatico...") {
        usuarioMultiLector.cambiaTipoLector(Fanatico)
        val autorNoPreferido = Autor()
        val libroNoLargo = Libro(autor = autorTesteo, paginas = 600)
        val libroLargo = Libro(autor = autorTesteo, paginas = 601)
        val libroNoCumpleCondicionesAnteriores = Libro(autor = autorNoPreferido, paginas = 600)
        it("lee un libro del autor preferido que no leyo y que no es largo") {
            usuarioMultiLector.agregarAutorPreferido(autorTesteo)
            usuarioMultiLector.tiempoDeLectura(libroNoLargo) shouldBe 1200
        }
        it("lee un libro del autor preferido que no leyo y que es largo") {
            usuarioMultiLector.agregarAutorPreferido(autorTesteo)
            usuarioMultiLector.tiempoDeLectura(libroLargo) shouldBe 1201
        }
        it("lee un libro que no cumple las condiciones anteriores") {
            usuarioMultiLector.tiempoDeLectura(libroNoCumpleCondicionesAnteriores) shouldBe 400
        }
    }

    describe("Dado un usuario lector recurrente...") {
        usuarioMultiLector.cambiaTipoLector(Recurrente)

        it("lee un libro por primera vez y no disminuye el tiempo de lectura promedio") {
            usuarioMultiLector.agregarLibroLeido(libroTesteo)
            usuarioMultiLector.tiempoDeLectura(libroTesteo) shouldBe 800
        }
        it("lee un libro por cuarta vez y diminuye el tiempo de lectura") {
            usuarioMultiLector.agregarLibroLeido(libroTesteo)
            usuarioMultiLector.agregarLibroLeido(libroTesteo)
            usuarioMultiLector.agregarLibroLeido(libroTesteo)
            usuarioMultiLector.agregarLibroLeido(libroTesteo)
            usuarioMultiLector.tiempoDeLectura(libroTesteo) shouldBe 768
        }
        it("lee un libro por sexta vez y diminuye el tiempo de lectura hasta el nivel requerido") {
            usuarioMultiLector.agregarLibroLeido(libroTesteo)
            usuarioMultiLector.agregarLibroLeido(libroTesteo)
            usuarioMultiLector.agregarLibroLeido(libroTesteo)
            usuarioMultiLector.agregarLibroLeido(libroTesteo)
            usuarioMultiLector.agregarLibroLeido(libroTesteo)
            usuarioMultiLector.agregarLibroLeido(libroTesteo)
            usuarioMultiLector.tiempoDeLectura(libroTesteo) shouldBe 760
        }
    }
})