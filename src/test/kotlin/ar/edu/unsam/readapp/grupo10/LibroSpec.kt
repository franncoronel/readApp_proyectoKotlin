package ar.edu.unsam.readapp.grupo10

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class LibroSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    describe("Test de funcionalidad del idioma") {
        val autorBengali = Autor(lenguaNativa = Lenguaje.BENGALI)
        val libro = Libro(autor = autorBengali).apply {
            this.agregarTraduccion(setOf(Lenguaje.ARABE, Lenguaje.ALEMAN, Lenguaje.HINDI))
        }
        it("el libro tendra 3 traducciones + lengua nativa del autor = 4 traducciones") {
            libro.idiomas().size shouldBe 4
        }
    }
    describe("Test de Libro desafiante") {
        val autorEspaniol = Autor(lenguaNativa = Lenguaje.ESPANIOL)
        describe("Dado un libro, es desafiante") {
            it("si es de lectura compleja o es largo") {
                //arrange
                val libroDesafiante = Libro(autor = autorEspaniol).apply {
                    this.agregarTraduccion(setOf(Lenguaje.ESPANIOL))
                }
                //assert
                libroDesafiante.lenguajeOriginal shouldBe Lenguaje.ESPANIOL
                libroDesafiante.idiomas().size shouldBe 1
                libroDesafiante.esDesafiante() shouldBe true
            }
        }
        describe("Dado un libro, no es desafiante") {
            it("si no es de lectura compleja ni es largo") {
                //arrange
                val libroNoDesafiante = Libro(
                    paginas = 600,
                    autor = autorEspaniol
                ).apply {
                    this.agregarTraduccion(setOf(Lenguaje.ALEMAN))
                }
                //assert
                libroNoDesafiante.idiomas().size shouldBe 2
                libroNoDesafiante.esDesafiante() shouldBe false
            }
        }
    }
    describe("Tests de Libro best-seller") {
        val autorIngles = Autor(lenguaNativa = Lenguaje.INGLES)
        describe("Dado un libro con suficientes ventas semanales, suficientes ediciones y suficientes traducciones") {
            it("Debería ser best-seller") {
                //Arrange
                val libroBestSellerPorVentasEdicionesTraducciones = Libro(
                    ediciones = 3,
                    autor = autorIngles
                ).apply {
                    this.agregarTraduccion(
                        setOf(
                            Lenguaje.ESPANIOL, Lenguaje.ALEMAN, Lenguaje.MANDARIN, Lenguaje.RUSO, Lenguaje.ARABE
                        )
                    )
                }
                //Assert
                libroBestSellerPorVentasEdicionesTraducciones.idiomas().size shouldBe 6
                libroBestSellerPorVentasEdicionesTraducciones.esBestSeller() shouldBe true
            }
        }
        describe("Dado un libro con suficientes ventas semanales, insuficientes ediciones y suficientes traducciones") {
            it("Debería ser best-seller") {
                //Arrange
                val libroBestSellerPorVentasYTraducciones = Libro(autor = autorIngles).apply {
                    this.agregarTraduccion(
                        setOf(
                            Lenguaje.ESPANIOL, Lenguaje.ALEMAN, Lenguaje.BENGALI, Lenguaje.MANDARIN, Lenguaje.RUSO
                        )
                    )
                }
                //Assert
                libroBestSellerPorVentasYTraducciones.idiomas().size shouldBe 6
                libroBestSellerPorVentasYTraducciones.esBestSeller() shouldBe true
            }
        }
        describe("Dado un libro sin las ventas semanales requeridas, suficientes ediciones e insuficientes traducciones") {
            it("No debería ser best-seller") {
                //Arrange
                val libroNoBestSellerPorVentas = Libro(
                    ediciones = 3,
                    ventasSemanales = 9999,
                    autor = autorIngles
                ).apply {
                    this.agregarTraduccion(setOf(Lenguaje.ESPANIOL, Lenguaje.FRANCES, Lenguaje.RUSO, Lenguaje.BENGALI))
                }
                //Assert
                libroNoBestSellerPorVentas.idiomas().size shouldBe 5
                libroNoBestSellerPorVentas.esBestSeller() shouldBe false
            }
        }
        describe("Dado un libro sin las ventas semanales requeridas pero suficientes ediciones y suficientes traducciones") {
            it("No debería ser best-seller") {
                //Arrange
                val libroNoBestSellerPorVentas = Libro(
                    ediciones = 3,
                    ventasSemanales = 9999,
                    autor = autorIngles
                ).apply {
                    this.agregarTraduccion(
                        setOf(
                            Lenguaje.ESPANIOL,
                            Lenguaje.ALEMAN,
                            Lenguaje.PORTUGUES,
                            Lenguaje.MANDARIN,
                            Lenguaje.RUSO
                        )
                    )
                }
                //Assert
                libroNoBestSellerPorVentas.idiomas().size shouldBe 6
                libroNoBestSellerPorVentas.esBestSeller() shouldBe false
            }
        }
        describe("Dado un libro con suficientes ventas semanales pero sin las suficientes ediciones o traducciones") {
            it("No debería ser best-seller") {
                //Arrange
                val libroNoBestSellerPorEdicionesOTraducciones = Libro(autor = autorIngles).apply {
                    this.agregarTraduccion(
                        setOf(
                            Lenguaje.PORTUGUES, Lenguaje.FRANCES, Lenguaje.JAPONES
                        )
                    )
                }
                //Assert
                libroNoBestSellerPorEdicionesOTraducciones.lenguajeOriginal shouldBe Lenguaje.INGLES
                libroNoBestSellerPorEdicionesOTraducciones.idiomas().size shouldBe 4
                libroNoBestSellerPorEdicionesOTraducciones.esBestSeller() shouldBe false
            }
        }
    }
})