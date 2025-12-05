# ClearLag 插件说明文档

## 插件简介
ClearLag 是一个用于 Minecraft 服务器的优化插件，可以帮助服务器清理地上的掉落物和不必要的实体，从而减轻服务器负载，提高服务器性能。

## 功能特点

- **自动清理掉落物**：定期清理地上的掉落物，可自定义清理间隔
- **自动清理实体**：可以选择是否清理实体，支持自定义清理间隔
- **白名单功能**：
  - 掉落物白名单：保护特定物品不被清理
  - 实体白名单：保护特定实体不被清理
- **警告系统**：
  - 清理前发送警告消息
  - 支持自定义警告消息
  - 可选择是否启用警告音效
  - 支持多个警告时间段
- **自定义消息**：支持自定义警告消息和清理完成消息

## 安装方法

1. 下载最新版本的 ClearLag 插件 JAR 文件
2. 将 JAR 文件放入服务器的 `plugins` 目录
3. 重启服务器，插件会自动生成配置文件

## 配置说明

插件的配置文件位于 `plugins/ClearLag/config.yml`，以下是配置项说明：

### 清理设置

```yaml
清理设置:
  清理凋落物: true  # 是否清理掉落物
  清理实体: false   # 是否清理实体
  清理间隔(秒): 300  # 清理间隔时间（秒）
```

### 警告设置

```yaml
警告设置:
  警告时间段: [60, 30, 10, 3, 2, 1]  # 清理前多少秒发送警告
  启用警告音效: true                  # 是否启用警告音效
  警告音效: block.note_block.pling    # 警告音效
  警告消息: '&a[大馋丫头]&e将在 &c{seconds} &e秒后吃掉地上的物品请注意!'  # 警告消息
```

### 消息设置

```yaml
消息设置:
  清理完成消息: '&a[大馋丫头]&a吃掉了 &e{drops} &a个掉落物 &a和 &e{entities} &a个实体&a!'  # 清理完成消息
```

### 白名单设置

```yaml
白名单设置:
  凋落物白名单:  # 不被清理的物品列表
    - SHULKER_BOX
    - WHITE_SHULKER_BOX
    - ORANGE_SHULKER_BOX
    - MAGENTA_SHULKER_BOX
    - LIGHT_BLUE_SHULKER_BOX
    - YELLOW_SHULKER_BOX
    - LIME_SHULKER_BOX
    - PINK_SHULKER_BOX
    - GRAY_SHULKER_BOX
    - LIGHT_GRAY_SHULKER_BOX
    - CYAN_SHULKER_BOX
    - PURPLE_SHULKER_BOX
    - BLUE_SHULKER_BOX
    - BROWN_SHULKER_BOX
    - GREEN_SHULKER_BOX
    - RED_SHULKER_BOX
    - BLACK_SHULKER_BOX
  实体白名单:  # 不被清理的实体列表
    - CHICKEN
    - COW
    - PIG
    - SHEEP
    - HORSE
    - DONKEY
    - MULE
    - LLAMA
    - TRADER_LLAMA
    - CAT
    - DOG
    - WOLF
    - OCELOT
    - PARROT
    - FOX
    - GOAT
    - RABBIT
    - BAT
    - TURTLE
    - SNOWMAN
    - IRON_GOLEM
    - VILLAGER
    - AXOLOTL
    - ALLAY
    - BOAT
    - CHEST_BOAT
    - MINECART
    - CHEST_MINECART
    - FURNACE_MINECART
    - TNT_MINECART
    - HOPPER_MINECART
    - COMMAND_BLOCK_MINECART
    - SKELETON_HORSE
    - ZOMBIE_HORSE
    - STRIDER
    - PANDA
    - BEE
    - WANDERING_TRADER
    - CAMEL
```

## 使用方法

1. **安装插件**：将插件 JAR 文件放入服务器的 `plugins` 目录并重启服务器
2. **配置插件**：修改 `plugins/ClearLag/config.yml` 文件，根据需要调整配置项
3. **重载配置**：使用 `/clearlag reload` 命令重载配置文件（需要 OP 权限）
4. **手动清理**：使用 `/clearlag clear` 命令手动清理掉落物和实体（需要 OP 权限）

## 命令说明

- `/clearlag reload`：重载配置文件
- `/clearlag clear`：立即清理所有掉落物和实体

## 权限说明

- `clearlag.reload`：允许重载配置文件
- `clearlag.clear`：允许手动清理掉落物和实体
- `clearlag.admin`：包含所有 ClearLag 权限

## 版本历史

### v1.0
- 初始版本
- 支持自动清理掉落物和实体
- 支持白名单功能
- 支持警告系统
- 支持自定义消息

## 作者信息

作者：大胖小子（随便起的名字和打CSGO的朋友互相开玩笑取得）

## 注意事项

1. 使用实体清理功能时，请确保已将重要实体（如村民、宠物等）添加到实体白名单中
2. 掉落物白名单中的物品类型名称必须使用大写（如 SHULKER_BOX）
3. 建议根据服务器实际情况调整清理间隔，避免影响玩家体验
4. 清理实体功能默认是关闭的，需要手动在配置文件中开启

## 联系与反馈

如果您在使用过程中遇到问题或有任何建议，欢迎联系作者反馈。
