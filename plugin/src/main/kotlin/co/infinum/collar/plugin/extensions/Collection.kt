package co.infinum.collar.plugin.extensions

internal infix fun <E> MutableCollection<in E>.append(elem: E): MutableCollection<in E> {
    this.add(elem)
    return this
}

internal infix fun <E> MutableCollection<in E>.appendAll(elems: Collection<E>) {
    this.addAll(elems)
}

internal inline operator fun <reified E> MutableCollection<in E>.plus(elem: E): MutableCollection<in E> {
    this.add(elem)
    return this
}