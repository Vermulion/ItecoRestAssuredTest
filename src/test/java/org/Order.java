package org;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class Order {
    private int petId;
    private int quantity;
    private int id;
    private String shipDate;
    private boolean complete;
    private String status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return petId == order.petId && quantity == order.quantity && id == order.id &&
                complete == order.complete && Objects.equals(shipDate, order.shipDate) &&
                Objects.equals(status, order.status);
    }
}
