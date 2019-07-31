package com.tzl.agriculture.fragment.vip.model;

/**
 * 优惠券
 *
 */
public class CouponMo {
    private String cardId;
    private String typeId;
    private String category;
    private String userId;
    private String shopIds;
    private String goodsIds;
    private String goodsCateids;
    private String cradName;
    private String cradNote;
    private String startEffective;
    private String endEffective;
    private String numEffective;
    private String amount;
    private String discount;//折扣
    private int consumeType;//0 折扣    1抵扣
    private int cardState;//0待使用   1已使用   2已过期
    private String receiveState;

    public CouponMo() {
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public int getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(int consumeType) {
        this.consumeType = consumeType;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getShopIds() {
        return shopIds;
    }

    public void setShopIds(String shopIds) {
        this.shopIds = shopIds;
    }

    public String getGoodsIds() {
        return goodsIds;
    }

    public void setGoodsIds(String goodsIds) {
        this.goodsIds = goodsIds;
    }

    public String getGoodsCateids() {
        return goodsCateids;
    }

    public void setGoodsCateids(String goodsCateids) {
        this.goodsCateids = goodsCateids;
    }

    public String getCradName() {
        return cradName;
    }

    public void setCradName(String cradName) {
        this.cradName = cradName;
    }

    public String getCradNote() {
        return cradNote;
    }

    public void setCradNote(String cradNote) {
        this.cradNote = cradNote;
    }

    public String getStartEffective() {
        return startEffective;
    }

    public void setStartEffective(String startEffective) {
        this.startEffective = startEffective;
    }

    public String getEndEffective() {
        return endEffective;
    }

    public void setEndEffective(String endEffective) {
        this.endEffective = endEffective;
    }

    public String getNumEffective() {
        return numEffective;
    }

    public void setNumEffective(String numEffective) {
        this.numEffective = numEffective;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getCardState() {
        return cardState;
    }

    public void setCardState(int cardState) {
        this.cardState = cardState;
    }

    public String getReceiveState() {
        return receiveState;
    }

    public void setReceiveState(String receiveState) {
        this.receiveState = receiveState;
    }
}
