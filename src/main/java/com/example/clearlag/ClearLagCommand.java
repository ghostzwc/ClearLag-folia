package com.example.clearlag;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ClearLagCommand implements CommandExecutor, TabCompleter {

    private final ClearLag plugin;

    public ClearLagCommand(ClearLag plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // 检查是否有OP权限
        if (!(sender.hasPermission("clearlag.admin") || sender.isOp())) {
            sender.sendMessage("&c你没有权限执行此命令!".replace('&', '§'));
            return true;
        }

        // 处理命令
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help":
                sendHelpMessage(sender);
                break;
            case "reload":
                reloadConfig(sender);
                break;
            case "clear":
                clearNow(sender);
                break;
            case "time":
                showTimeUntilNextClear(sender);
                break;
            default:
                sender.sendMessage("&c未知命令! 输入 /clearlag help 查看帮助.".replace('&', '§'));
                break;
        }

        return true;
    }

    // 发送帮助信息
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage("&e=== ClearLag 帮助 ===".replace('&', '§'));
        sender.sendMessage("&a/clearlag help &7- 显示此帮助信息".replace('&', '§'));
        sender.sendMessage("&a/clearlag reload &7- 重新加载配置文件".replace('&', '§'));
        sender.sendMessage("&a/clearlag clear &7- 立即清理凋落物和实体".replace('&', '§'));
        sender.sendMessage("&a/clearlag time &7- 查询距离下次清理还有多久".replace('&', '§'));
        sender.sendMessage("&e插件版本: 1.0 | 专为Folia服务端设计".replace('&', '§'));
    }

    // 重新加载配置文件
    private void reloadConfig(CommandSender sender) {
        plugin.loadConfig();
        plugin.restartTasks(); // 重新启动任务以应用新的配置
        sender.sendMessage("&a配置文件已重新加载!".replace('&', '§'));
    }
    
    // 立即清理凋落物和实体
    private void clearNow(CommandSender sender) {
        plugin.clearNow();
        sender.sendMessage("&a已执行立即清理!".replace('&', '§'));
    }
    
    // 显示距离下次清理还有多久
    private void showTimeUntilNextClear(CommandSender sender) {
        long timeUntil = plugin.getTimeUntilNextClear();
        sender.sendMessage(("&e距离下次自动清理还有 &a" + timeUntil + " &e秒!").replace('&', '§'));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.add("help");
            completions.add("reload");
            completions.add("clear");
            completions.add("time");
        }
        
        return completions;
    }
}