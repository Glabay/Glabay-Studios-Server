package io.xeros.model.entity

import io.xeros.model.entity.player.Player
import java.lang.ref.WeakReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

inline fun <reified T: Any?> persistentAttribute(key: String, defaultValue: T? = null) =
    attribute<Player, T>(key, defaultValue, persistent = true)

inline fun <reified E : Entity, reified T : Any?> attribute(
    key: String,
    defaultValue: T? = null,
    persistent: Boolean = false,
): ReadWriteProperty<E, T> =
    attribute(key, persistent) { defaultValue as T }

inline fun <reified E : Entity, reified T : Any?> attribute(
    key: String,
    persistent: Boolean = false,
    crossinline defaultValueFunction: (E.() -> T),
): ReadWriteProperty<E, T> {
    return object : ReadWriteProperty<E, T> {

        override fun getValue(thisRef: E, property: KProperty<*>): T {
            var value = if (persistent)
                getPersistentAttributes(thisRef)[key]
            else
                thisRef.temporaryAttributes[key]
            if (value == null){
                value = defaultValueFunction(thisRef)
                if (value != null) {
                    if (persistent)
                        getPersistentAttributes(thisRef)[key] = value
                    else
                        thisRef.temporaryAttributes[key] = value
                }
            }
            return (when {
                value is Number && value::class != T::class -> when(T::class) {
                    Byte::class -> value.toByte()
                    Short::class -> value.toShort()
                    Int::class -> value.toInt()
                    Float::class -> value.toFloat()
                    Double::class -> value.toDouble()
                    Long::class -> value.toLong()
                    else -> error("Invalid number type (key=$key, type=${T::class})")
                }
                else -> value
            }) as T
        }

        override fun setValue(thisRef: E, property: KProperty<*>, value: T) {
            if (value == null){
                if (persistent)
                    getPersistentAttributes(thisRef).remove(key)
                else
                    thisRef.temporaryAttributes.remove(key)
            } else {
                if (persistent)
                    getPersistentAttributes(thisRef)[key] = value
                else
                    thisRef.temporaryAttributes[key] = value
            }
        }

        private fun getPersistentAttributes(thisRef: E) = if (thisRef !is Player)
            error("Persistent attributes are only supported for Player entities (key=$key, ref=$thisRef)")
        else
            thisRef.finalAttributes
    }
}


inline fun <reified T> weakReferenceAttribute(key: String): ReadWriteProperty<Entity, T?> =
    object : ReadWriteProperty<Entity, T?> {
        override fun getValue(thisRef: Entity, property: KProperty<*>): T? {
            val element = thisRef.temporaryAttributes[key]
            if (element is WeakReference<*>) {
                val result = element.get()
                if (result is T) {
                    return result
                }
            }
            return null
        }

        override fun setValue(thisRef: Entity, property: KProperty<*>, value: T?) {
            if (value == null) {
                thisRef.temporaryAttributes.remove(key)
            } else {
                thisRef.temporaryAttributes[key] = WeakReference(value)
            }
        }
    }
