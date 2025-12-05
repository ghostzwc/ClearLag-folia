package com.example.clearlag;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.TimeUnit;

public class ClearLag extends JavaPlugin {

    private FileConfiguration config;
    private File configFile;
    private ScheduledTask clearTask;
    private List<ScheduledTask> warningTasks = new ArrayList<>();
    private long lastClearTime = 0;
    private long nextClearTime = 0;

    // 配置项
    private boolean clearDropsEnabled;
    private boolean clearEntitiesEnabled;
    private long clearInterval;
    private List<Long> warningIntervals;
    private boolean warningSoundEnabled;
    private String warningSound;
    private String warningMessage;
    private String clearCompleteMessage;
    private List<String> dropWhitelist;
    private List<String> entityWhitelist;

    @Override
    public void onEnable() {
        // 加载配置
        loadConfig();
        
        // 注册命令
        getCommand("clearlag").setExecutor(new ClearLagCommand(this));
        
        // 启动清理任务
        startTasks();
        
        getLogger().info("ClearLag插件已启用 - 专为Folia服务端设计");
    }

    @Override
    public void onDisable() {
        // 取消所有任务
        if (clearTask != null) {
            clearTask.cancel();
        }
        for (ScheduledTask task : warningTasks) {
            if (task != null) {
                task.cancel();
            }
        }
        
        getLogger().info("ClearLag插件已禁用");
    }

    // 加载配置文件
    public void loadConfig() {
        configFile = new File(getDataFolder(), "config.yml");
        
        // 如果配置文件不存在，创建默认配置
        if (!configFile.exists()) {
            saveDefaultConfigFile();
        }
        
        config = YamlConfiguration.loadConfiguration(configFile);
        
        // 读取配置项
        clearDropsEnabled = config.getBoolean("清理设置.清理凋落物", true);
        clearEntitiesEnabled = config.getBoolean("清理设置.清理实体", false);
        clearInterval = config.getLong("清理设置.清理间隔(秒)", 300);
        
        // 读取多个警告时间段
        List<Long> intervals = config.getLongList("警告设置.警告时间段");
        if (intervals.isEmpty()) {
            // 兼容旧配置
            long oldInterval = config.getLong("警告设置.警告间隔(秒)", 30);
            intervals = Collections.singletonList(oldInterval);
        }
        warningIntervals = intervals;
        
        warningSoundEnabled = config.getBoolean("警告设置.启用警告音效", true);
        warningSound = config.getString("警告设置.警告音效", "block.note_block.pling");
        warningMessage = config.getString("警告设置.警告消息", "&a[大馋丫头]&e将在 &c{seconds} &e秒后吃掉地上的物品请注意!");
        clearCompleteMessage = config.getString("消息设置.清理完成消息", "&a[大馋丫头]&a吃掉了 &e{drops} &a个掉落物 &a和 &e{entities} &a个实体&a!");
        // 读取凋落物白名单并转换为大写，确保匹配时大小写不敏感（符合服务器实际情况）
        List<String> rawDropWhitelist = config.getStringList("白名单设置.凋落物白名单");
        dropWhitelist = new ArrayList<>();
        for (String item : rawDropWhitelist) {
            dropWhitelist.add(item.toUpperCase());
        }
        
        // 读取实体白名单并转换为大写，确保匹配时大小写不敏感
        List<String> rawEntityWhitelist = config.getStringList("白名单设置.实体白名单");
        entityWhitelist = new ArrayList<>();
        for (String entity : rawEntityWhitelist) {
            entityWhitelist.add(entity.toUpperCase());
        }
    }

    // 保存默认配置
    private void saveDefaultConfigFile() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        config = new YamlConfiguration();
        
        // 设置默认配置
        config.set("清理设置.清理凋落物", true);
        config.set("清理设置.清理实体", false);
        config.set("清理设置.清理间隔(秒)", 300);
        
        // 默认多个警告时间段
        List<Long> defaultIntervals = Arrays.asList(60L, 30L, 10L, 3L, 2L, 1L);
        config.set("警告设置.警告时间段", defaultIntervals);
        
        config.set("警告设置.启用警告音效", true);
        config.set("警告设置.警告音效", "block.note_block.pling");
        config.set("警告设置.警告消息", "&a[大馋丫头]&e将在 &c{seconds} &e秒后吃掉地上的物品请注意!");
        
        // 消息设置
        config.set("消息设置.清理完成消息", "&a[大馋丫头]&a吃掉了 &e{drops} &a个掉落物 &a和 &e{entities} &a个实体&a!");
        
        // 默认凋落物白名单 - 各种颜色和命名的潜影盒（使用大写，符合服务器实际情况）
        List<String> defaultDropWhitelist = Arrays.asList(
                "SHULKER_BOX",
                "WHITE_SHULKER_BOX",
                "ORANGE_SHULKER_BOX",
                "MAGENTA_SHULKER_BOX",
                "LIGHT_BLUE_SHULKER_BOX",
                "YELLOW_SHULKER_BOX",
                "LIME_SHULKER_BOX",
                "PINK_SHULKER_BOX",
                "GRAY_SHULKER_BOX",
                "LIGHT_GRAY_SHULKER_BOX",
                "CYAN_SHULKER_BOX",
                "PURPLE_SHULKER_BOX",
                "BLUE_SHULKER_BOX",
                "BROWN_SHULKER_BOX",
                "GREEN_SHULKER_BOX",
                "RED_SHULKER_BOX",
                "BLACK_SHULKER_BOX"
        );
        config.set("白名单设置.凋落物白名单", defaultDropWhitelist);
        
        // 默认实体白名单 - 动物、船、矿车
        List<String> defaultEntityWhitelist = Arrays.asList(
                "CHICKEN",
                "COW",
                "PIG",
                "SHEEP",
                "HORSE",
                "DONKEY",
                "MULE",
                "LLAMA",
                "TRADER_LLAMA",
                "CAT",
                "WOLF",
                "OCELOT",
                "FOX",
                "RABBIT",
                "TURTLE",
                "PARROT",
                "PANDA",
                "POLAR_BEAR",
                "BEE",
                "STRIDER",
                "GOAT",
                "AXOLOTL",
                "FROG",
                "SALMON",
                "COD",
                "PUFFERFISH",
                "TROPICAL_FISH",
                "DOLPHIN",
                "TADPOLE",
                "WARDEN",
                "BOAT",
                "CHEST_BOAT",
                "MINECART",
                "CHEST_MINECART",
                "FURNACE_MINECART",
                "TNT_MINECART",
                "HOPPER_MINECART",
                "COMMAND_BLOCK_MINECART"
        );
        config.set("白名单设置.实体白名单", defaultEntityWhitelist);
        
        try {
            config.save(configFile);
        } catch (Exception e) {
            getLogger().severe("保存默认配置文件失败: " + e.getMessage());
        }
    }

    // 重启清理和警告任务（用于配置文件重新加载后）
    public void restartTasks() {
        startTasks();
    }
    
    // 启动清理和警告任务
    private void startTasks() {
        // 取消现有任务
        if (clearTask != null) {
            clearTask.cancel();
        }
        for (ScheduledTask task : warningTasks) {
            if (task != null) {
                task.cancel();
            }
        }
        warningTasks.clear();
        
        // 清理任务 - 立即执行，然后每clearInterval秒执行一次
        clearTask = Bukkit.getAsyncScheduler().runAtFixedRate(this, task -> {
            Bukkit.getGlobalRegionScheduler().execute(this, () -> {
                clearLootAndEntities();
            });
        }, 0, clearInterval, TimeUnit.SECONDS);
        
        // 设置下一次清理时间（初始值为当前时间加上清理间隔）
        // 注意：实际清理后会在clearLootAndEntities方法中更新nextClearTime
        nextClearTime = System.currentTimeMillis() + (clearInterval * 1000);
        
        // 为每个警告时间段创建任务
        if (!warningIntervals.isEmpty()) {
            // 对警告时间段进行排序，确保从大到小
            List<Long> sortedIntervals = new ArrayList<>(warningIntervals);
            sortedIntervals.sort(Collections.reverseOrder());
            
            for (long interval : sortedIntervals) {
                if (interval > 0 && interval < clearInterval) {
                    // 计算每个警告任务的延迟和周期
                    long firstDelay = clearInterval - interval;
                    ScheduledTask task = Bukkit.getAsyncScheduler().runAtFixedRate(this, scheduledTask -> {
                        Bukkit.getGlobalRegionScheduler().execute(this, () -> {
                            sendWarningMessage(interval);
                        });
                    }, firstDelay, clearInterval, TimeUnit.SECONDS);
                    warningTasks.add(task);
                }
            }
        }
    }

    // 发送警告消息
    private void sendWarningMessage(long seconds) {
        String message = warningMessage.replace("{seconds}", String.valueOf(seconds));
        message = message.replace('&', '§');
        
        Bukkit.broadcastMessage(message);
        
        // 如果启用了警告音效
        if (warningSoundEnabled) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), warningSound, 1.0f, 1.0f);
            }
        }
    }

    // 清理凋落物和实体
    private void clearLootAndEntities() {
        lastClearTime = System.currentTimeMillis();
        
        // 使用线程安全的计数器
        AtomicInteger dropsCleared = new AtomicInteger(0);
        AtomicInteger entitiesCleared = new AtomicInteger(0);
        
        // 获取世界数量
        int worldCount = Bukkit.getWorlds().size();
        AtomicInteger worldsProcessed = new AtomicInteger(0);
        
        // 在Folia中，我们需要使用全局调度器来协调清理操作
        for (World world : Bukkit.getWorlds()) {
            // 获取世界的出生点作为区域调度的位置
            Location spawnLocation = world.getSpawnLocation();
            
            // 使用区域调度器在正确的区域线程上执行操作
            Bukkit.getRegionScheduler().execute(this, spawnLocation, () -> {
                // 清理凋落物
                if (clearDropsEnabled) {
                    // 在Folia中，使用world.getEntitiesByClass需要在正确的线程上
                    List<Item> itemsToRemove = new ArrayList<>();
                    
                    // 遍历所有实体，筛选出Item类型
                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof Item) {
                            Item item = (Item) entity;
                            if (!isItemWhitelisted(item.getItemStack())) {
                                itemsToRemove.add(item);
                            }
                        }
                    }
                    
                    // 批量删除
                    for (Item item : itemsToRemove) {
                        item.remove();
                    }
                    // 更新计数器
                    dropsCleared.addAndGet(itemsToRemove.size());
                }
                
                // 清理实体
                if (clearEntitiesEnabled) {
                    List<Entity> entitiesToRemove = new ArrayList<>();
                    
                    for (Entity entity : world.getEntities()) {
                        // 跳过玩家
                        if (entity instanceof Player) {
                            continue;
                        }
                        
                        // 跳过Item类型的实体（凋落物已经在前面的逻辑中处理过了）
                        if (entity instanceof Item) {
                            continue;
                        }
                        
                        // 跳过白名单中的实体
                        if (isEntityWhitelisted(entity)) {
                            continue;
                        }
                        
                        // 清理所有非白名单实体（除了玩家和凋落物）
                        entitiesToRemove.add(entity);
                    }
                    
                    // 批量删除
                    for (Entity entity : entitiesToRemove) {
                        entity.remove();
                    }
                    // 更新计数器
                    entitiesCleared.addAndGet(entitiesToRemove.size());
                }
                
                // 检查是否所有世界都已处理完成
                if (worldsProcessed.incrementAndGet() == worldCount) {
                    // 所有世界处理完成，发送消息
                    Bukkit.getGlobalRegionScheduler().execute(this, () -> {
                        String message = clearCompleteMessage;
                        // 确保替换所有占位符
                        message = message.replace("{drops}", String.valueOf(dropsCleared.get()))
                                        .replace("{entities}", String.valueOf(entitiesCleared.get()));
                        Bukkit.broadcastMessage(message.replace('&', '§'));
                        
                        // 更新下一次清理时间（基于配置文件中的清理间隔）
                        nextClearTime = System.currentTimeMillis() + (clearInterval * 1000);
                    });
                }
            });
        }
    }

    // 检查物品是否在白名单中
    private boolean isItemWhitelisted(ItemStack item) {
        if (item == null) {
            return false;
        }
        
        // 检查物品类型（使用大写匹配，符合服务器实际情况）
        String materialName = item.getType().name();
        if (dropWhitelist.contains(materialName)) {
            return true;
        }
        
        // 检查是否有自定义名称
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return true;
        }
        
        return false;
    }

    // 检查实体是否在白名单中
    private boolean isEntityWhitelisted(Entity entity) {
        if (entity == null) {
            return false;
        }
        
        String entityType = entity.getType().name();
        return entityWhitelist.contains(entityType);
    }

    // 获取配置文件
    public FileConfiguration getConfig() {
        return config;
    }
    
    // 获取距离下次清理的时间（秒）
    public long getTimeUntilNextClear() {
        // 确保nextClearTime始终有一个有效的值
        if (nextClearTime == 0) {
            // 如果nextClearTime未初始化，重新计算
            nextClearTime = System.currentTimeMillis() + (clearInterval * 1000);
        }
        long timeUntil = (nextClearTime - System.currentTimeMillis()) / 1000;
        return Math.max(0, timeUntil);
    }
    
    // 立即清理凋落物和实体
    public void clearNow() {
        clearLootAndEntities();
    }
}