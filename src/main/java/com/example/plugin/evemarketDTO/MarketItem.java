package com.example.plugin.evemarketDTO;

public class MarketItem {

  ItemType type;
  ItemStats sell_stats;
  ItemStats buy_stats;

  public ItemType getType()
  {
    return type;
  }

  public void setType(ItemType type)
  {
    this.type = type;
  }

  public ItemStats getSell_stats()
  {
    return sell_stats;
  }

  public void setSell_stats(ItemStats sell_stats)
  {
    this.sell_stats = sell_stats;
  }

  public ItemStats getBuy_stats()
  {
    return buy_stats;
  }

  public void setBuy_stats(ItemStats buy_stats)
  {
    this.buy_stats = buy_stats;
  }
}
