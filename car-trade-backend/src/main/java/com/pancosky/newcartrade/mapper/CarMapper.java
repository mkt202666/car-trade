package com.pancosky.newcartrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pancosky.newcartrade.entity.CarSource;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface CarMapper extends BaseMapper<CarSource> {

    @Update("UPDATE car_sources SET view_count = view_count + 1 WHERE id = #{carId}")
    void incrementViewCount(@Param("carId") Long carId);

    @Update("UPDATE car_sources SET favorite_count = favorite_count + 1 WHERE id = #{carId}")
    void incrementFavoriteCount(@Param("carId") Long carId);

    @Update("UPDATE car_sources SET favorite_count = favorite_count - 1 WHERE id = #{carId}")
    void decrementFavoriteCount(@Param("carId") Long carId);
}
