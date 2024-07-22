package ar.edu.unsam.readapp.grupo10

class Repositorio<T : Entidad> {
    var entidades = mutableListOf<T>()
    var identificador = 1

    fun agregar(entidad: T) {
//        entidad.estaBienConstruido()
        noFueAgregado(entidad)
        entidad.asignarID(identificador)
        identificador += 1
        entidades.add(entidad)
    }

    fun noFueAgregado(entidad: T) {
        if (entidades.find { it === entidad } != null)
            throw BusinessException("Este elemento ya se encuentra en el repositorio")
    }

    fun borrar(entidad: T) {
        entidades.remove(obtenerPorId(entidad.id()))
    }

    fun actualizar(entidadNueva: T) {
//        entidadNueva.estaBienConstruido()
        val objetoAModificar = obtenerPorId(entidadNueva.id())
        val indice = entidades.indexOf(objetoAModificar)
        entidades[indice] = entidadNueva
    }

    fun obtenerPorId(idBuscado: Int): T {
        val busqueda = entidades.find { it.id() == idBuscado }
        if (busqueda == null)
            throw BusinessException("No se encontró objeto con dicho ID")
        return busqueda
    }

    fun buscar(busqueda: String): List<T> = entidades.filter { it.condicionBusqueda(busqueda) }
}

interface Entidad {
    fun condicionBusqueda(busqueda: String): Boolean

    fun asignarID(identificador: Int)

    fun id(): Int

    fun estaBienConstruido()
}
