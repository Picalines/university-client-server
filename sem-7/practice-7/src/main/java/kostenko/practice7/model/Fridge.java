package kostenko.practice7.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Table("fridges")
public class Fridge {
    @Id
    private Long id;
    @Column("type")
    private String type;
    @Column("color")
    private String color;
    @Column("size")
    private Float size;
    @Column("price")
    private Float price;
}
