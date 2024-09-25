package com.hwang.reggie.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hwang.reggie.entity.Setmeal;
import com.hwang.reggie.entity.SetmealDish;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SetmealDto extends Setmeal {
    private  List<SetmealDish>  setmealDishes;

    private  String  categoryName;
}
