package com.salvi.prova1;

public class ProductViewModel {

    public ProductViewModel() { };

    public ProductViewModel(
            String id,
            String product_name,
            String brand,
            String erp_id,
            String expire_at
    ){
        this.id = id;
        this.product_name = product_name;
        this.brand = brand;
        this.erp_id = erp_id;
        this.expire_at = expire_at;
    }

    public String id;
    public String product_name;
    public String brand;
    public String erp_id;
    public String expire_at;


}
