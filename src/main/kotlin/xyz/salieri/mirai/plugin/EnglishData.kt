package xyz.salieri.mirai.plugin

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.data.PluginDataExtensions.withDefault
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

var CommandSenderOnMessage<*>.coin: Int by EnglishUserData.sender()

object EnglishUserData : AutoSavePluginData("user"){
    @ValueDescription("Key 是QQ号，Value是coin数值")
    var coin by value<MutableMap<Long, Int>>().withDefault { 0 }
}

fun <T, K, V> AbstractPluginData.delegate(key: T.() -> K) = object : ReadWriteProperty<T, V> {
    override fun getValue(thisRef: T, property: KProperty<*>): V {
        return findBackingFieldValue<MutableMap<K, V>>(property.name)!!.value.getValue(thisRef.key())
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        findBackingFieldValue<MutableMap<K, V>>(property.name)!!.value[thisRef.key()] = value
    }
}

fun <V> AbstractPluginData.sender() = delegate<CommandSenderOnMessage<*>, Long, V> { fromEvent.sender.id }