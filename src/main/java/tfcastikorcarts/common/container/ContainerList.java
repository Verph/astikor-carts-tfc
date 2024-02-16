package tfcastikorcarts.common.container;

import java.util.Locale;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

import tfcastikorcarts.util.AstikorHelpers;

public enum ContainerList
{
    SIZE_0("1x1", 1, 1, 1, 184, 184, 256, 256),
    SIZE_1("1x9", 9, 1, 9, 184, 132, 256, 256),
    SIZE_2("2x9", 18, 2, 9, 184, 150, 256, 256),
    SIZE_3("3x9", 27, 3, 9, 184, 168, 256, 256),
    SIZE_4("4x9", 36, 4, 9, 184, 186, 256, 256),
    SIZE_5("5x9", 45, 5, 9, 184, 204, 256, 256),
    SIZE_6("6x9", 54, 6, 9, 184, 222, 256, 256),
    SIZE_7("7x9", 63, 7, 9, 184, 240, 256, 256),
    SIZE_8("8x9", 72, 8, 9, 184, 258, 256, 276),
    SIZE_9("9x9", 81, 9, 9, 184, 276, 256, 276),
    SIZE_10("9x10", 90, 9, 10, 202, 276, 256, 276),
    SIZE_11("9x11", 99, 9, 11, 220, 276, 256, 276),
    SIZE_12("9x12", 108, 9, 12, 238, 276, 256, 276),
    SIZE_13("9x13", 117, 9, 13, 256, 276, 256, 276);

    private static final ContainerList[] VALUES = values();

    public static ContainerList valueOf(int i)
    {
        return i >= 0 && i < VALUES.length ? VALUES[i] : SIZE_1;
    }

    public final String serializedName;
    public final String name;
    public final int size;
    public final int rows;
    public final int columns;
    public final int xSize;
    public final int ySize;
    public final ResourceLocation guiTexture;
    public final int textureXSize;
    public final int textureYSize;

    ContainerList(String name, int size, int rows, int columns, int xSize, int ySize, int textureXSize, int textureYSize)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.name = name.toLowerCase(Locale.ROOT);
        this.size = size;
        this.rows = rows;
        this.columns = columns;
        this.xSize = xSize;
        this.ySize = ySize;
        this.guiTexture = AstikorHelpers.identifier("textures/gui/" + name.toLowerCase(Locale.ROOT) + ".png");
        this.textureXSize = textureXSize;
        this.textureYSize = textureYSize;
    }

    public String getName()
    {
        return name.toLowerCase(Locale.ROOT);
    }

    public String getSerializedName()
    {
        return serializedName.toLowerCase(Locale.ROOT);
    }

    public ResourceLocation getResourceLocation()
    {
        return guiTexture;
    }

    public int getRows()
    {
        return rows;
    }

    public int getColumns()
    {
        return columns;
    }

    public int getSize()
    {
        return size;
    }

    public MenuType<?> getMenuType()
    {
        switch (this)
        {
            case SIZE_0:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_0.get();
            case SIZE_1:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_1.get();
            case SIZE_2:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_2.get();
            case SIZE_3:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_3.get();
            case SIZE_4:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_4.get();
            case SIZE_5:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_5.get();
            case SIZE_6:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_6.get();
            case SIZE_7:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_7.get();
            case SIZE_8:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_8.get();
            case SIZE_9:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_9.get();
            case SIZE_10:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_10.get();
            case SIZE_11:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_11.get();
            case SIZE_12:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_12.get();
            case SIZE_13:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_13.get();
            default:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_6.get();
        }
    }

    public boolean isSmallerThan(ContainerList other)
    {
        return this.ordinal() < other.ordinal();
    }

    public boolean isEqualOrSmallerThan(ContainerList other)
    {
        return this.ordinal() <= other.ordinal();
    }

    public static MenuType<?> getMenuType(ContainerList container)
    {
        switch (container)
        {
            case SIZE_0:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_0.get();
            case SIZE_1:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_1.get();
            case SIZE_2:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_2.get();
            case SIZE_3:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_3.get();
            case SIZE_4:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_4.get();
            case SIZE_5:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_5.get();
            case SIZE_6:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_6.get();
            case SIZE_7:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_7.get();
            case SIZE_8:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_8.get();
            case SIZE_9:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_9.get();
            case SIZE_10:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_10.get();
            case SIZE_11:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_11.get();
            case SIZE_12:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_12.get();
            case SIZE_13:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_13.get();
            default:
                return ContainerTypes.SUPPLY_CART_CONTAINER_SIZE_6.get();
        }
    }

    public static int getRows(ContainerList container)
    {
        return container.getRows();
    }

    public static int getColumns(ContainerList container)
    {
        return container.getColumns();
    }

    public static int getSlots(ContainerList container)
    {
        return container.getSize();
    }
}
