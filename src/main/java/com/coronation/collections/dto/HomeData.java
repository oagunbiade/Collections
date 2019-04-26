package com.coronation.collections.dto;

/**
 * Created by Toyin on 4/24/19.
 */
public class HomeData {
    private Long organization;
    private Long merchant;
    private Long product;
    private Long payment;
    private Long distributor;
    private Long user;

    public Long getOrganization() {
        return organization;
    }

    public void setOrganization(Long organization) {
        this.organization = organization;
    }

    public Long getMerchant() {
        return merchant;
    }

    public void setMerchant(Long merchant) {
        this.merchant = merchant;
    }

    public Long getProduct() {
        return product;
    }

    public void setProduct(Long product) {
        this.product = product;
    }

    public Long getPayment() {
        return payment;
    }

    public void setPayment(Long payment) {
        this.payment = payment;
    }

    public Long getDistributor() {
        return distributor;
    }

    public void setDistributor(Long distributor) {
        this.distributor = distributor;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }
}
