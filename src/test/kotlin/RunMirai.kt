package xyz.salieri.english

import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.enable
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.load
import net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader
import xyz.salieri.mirai.plugin.EnglishHelperPlugin

suspend fun main() {
    MiraiConsoleTerminalLoader.startAsDaemon()
    EnglishHelperPlugin.load()
    EnglishHelperPlugin.enable()

    val bot = MiraiConsole.addBot(234867252, "a135819183272333") {
        fileBasedDeviceInfo()
    }.alsoLogin()

    MiraiConsole.job.join()
}