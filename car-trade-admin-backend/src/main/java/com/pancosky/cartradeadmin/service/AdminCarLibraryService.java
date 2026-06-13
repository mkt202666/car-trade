package com.pancosky.cartradeadmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pancosky.cartradeadmin.common.BusinessException;
import com.pancosky.cartradeadmin.dto.*;
import com.pancosky.cartradeadmin.entity.AdminCarBrand;
import com.pancosky.cartradeadmin.entity.AdminCarModel;
import com.pancosky.cartradeadmin.entity.AdminCarSeries;
import com.pancosky.cartradeadmin.mapper.AdminCarBrandMapper;
import com.pancosky.cartradeadmin.mapper.AdminCarModelMapper;
import com.pancosky.cartradeadmin.mapper.AdminCarSeriesMapper;
import com.pancosky.cartradeadmin.vo.CarBrandVO;
import com.pancosky.cartradeadmin.vo.CarModelVO;
import com.pancosky.cartradeadmin.vo.CarSeriesVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminCarLibraryService {

    @Autowired
    private AdminCarBrandMapper adminCarBrandMapper;

    @Autowired
    private AdminCarSeriesMapper adminCarSeriesMapper;

    @Autowired
    private AdminCarModelMapper adminCarModelMapper;

    // ==================== 品牌 ====================

    @Cacheable(value = "car-library", key = "'brands:' + (#status ?: 'ALL')")
    public List<CarBrandVO> listBrands(String status) {
        LambdaQueryWrapper<AdminCarBrand> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            wrapper.eq(AdminCarBrand::getStatus, status);
        }
        wrapper.orderByAsc(AdminCarBrand::getId);
        List<AdminCarBrand> brands = adminCarBrandMapper.selectList(wrapper);
        return brands.stream().map(this::toBrandVO).collect(Collectors.toList());
    }

    @CacheEvict(value = "car-library", allEntries = true)
    public CarBrandVO createBrand(CarBrandDTO dto) {
        AdminCarBrand brand = new AdminCarBrand();
        brand.setName(dto.getName());
        brand.setLogoUrl(dto.getLogoUrl());
        brand.setFirstLetter(dto.getName() != null && !dto.getName().isEmpty() ? dto.getName().substring(0, 1).toUpperCase() : null);
        brand.setSortOrder(0);
        brand.setStatus("ACTIVE");
        adminCarBrandMapper.insert(brand);
        return toBrandVO(brand);
    }

    @CacheEvict(value = "car-library", allEntries = true)
    public CarBrandVO updateBrand(Integer id, CarBrandDTO dto) {
        AdminCarBrand brand = adminCarBrandMapper.selectById(id);
        if (brand == null) {
            throw new BusinessException(404, "品牌不存在");
        }
        if (dto.getName() != null) brand.setName(dto.getName());
        if (dto.getLogoUrl() != null) brand.setLogoUrl(dto.getLogoUrl());
        adminCarBrandMapper.updateById(brand);
        return toBrandVO(brand);
    }

    @CacheEvict(value = "car-library", allEntries = true)
    @Transactional
    public void deleteBrand(Integer id) {
        AdminCarBrand brand = adminCarBrandMapper.selectById(id);
        if (brand == null) {
            throw new BusinessException(404, "品牌不存在");
        }
        // 级联删除关联的车系
        LambdaQueryWrapper<AdminCarSeries> seriesWrapper = new LambdaQueryWrapper<AdminCarSeries>()
                .eq(AdminCarSeries::getBrandId, id);
        List<AdminCarSeries> seriesList = adminCarSeriesMapper.selectList(seriesWrapper);
        for (AdminCarSeries series : seriesList) {
            // 级联删除关联的车型
            LambdaQueryWrapper<AdminCarModel> modelWrapper = new LambdaQueryWrapper<AdminCarModel>()
                    .eq(AdminCarModel::getSeriesId, series.getId());
            adminCarModelMapper.delete(modelWrapper);
            adminCarSeriesMapper.deleteById(series.getId());
        }
        adminCarBrandMapper.deleteById(id);
    }

    // ==================== 车系 ====================

    @Cacheable(value = "car-library", key = "'series:' + (#brandId ?: 'all') + ':' + (#status ?: 'ALL')")
    public List<CarSeriesVO> listSeries(Long brandId, String status) {
        LambdaQueryWrapper<AdminCarSeries> wrapper = new LambdaQueryWrapper<>();
        if (brandId != null) {
            wrapper.eq(AdminCarSeries::getBrandId, brandId);
        }
        if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            wrapper.eq(AdminCarSeries::getStatus, status);
        }
        wrapper.orderByAsc(AdminCarSeries::getId);
        List<AdminCarSeries> seriesList = adminCarSeriesMapper.selectList(wrapper);
        return seriesList.stream().map(this::toSeriesVO).collect(Collectors.toList());
    }

    @CacheEvict(value = "car-library", allEntries = true)
    public CarSeriesVO createSeries(CarSeriesDTO dto) {
        AdminCarSeries series = new AdminCarSeries();
        series.setBrandId(dto.getBrandId() != null ? dto.getBrandId().intValue() : null);
        series.setName(dto.getName());
        series.setSortOrder(0);
        series.setStatus("ACTIVE");
        adminCarSeriesMapper.insert(series);
        return toSeriesVO(series);
    }

    @CacheEvict(value = "car-library", allEntries = true)
    public CarSeriesVO updateSeries(Long id, CarSeriesDTO dto) {
        AdminCarSeries series = adminCarSeriesMapper.selectById(id);
        if (series == null) {
            throw new BusinessException(404, "车系不存在");
        }
        if (dto.getBrandId() != null) series.setBrandId(dto.getBrandId().intValue());
        if (dto.getName() != null) series.setName(dto.getName());
        adminCarSeriesMapper.updateById(series);
        return toSeriesVO(series);
    }

    @CacheEvict(value = "car-library", allEntries = true)
    @Transactional
    public void deleteSeries(Long id) {
        AdminCarSeries series = adminCarSeriesMapper.selectById(id);
        if (series == null) {
            throw new BusinessException(404, "车系不存在");
        }
        // 级联删除关联的车型
        LambdaQueryWrapper<AdminCarModel> modelWrapper = new LambdaQueryWrapper<AdminCarModel>()
                .eq(AdminCarModel::getSeriesId, id);
        adminCarModelMapper.delete(modelWrapper);
        adminCarSeriesMapper.deleteById(id);
    }

    // ==================== 车型 ====================

    @Cacheable(value = "car-library", key = "'models:' + (#seriesId ?: 'all') + ':' + (#status ?: 'ALL')")
    public List<CarModelVO> listModels(Long seriesId, String status) {
        LambdaQueryWrapper<AdminCarModel> wrapper = new LambdaQueryWrapper<>();
        if (seriesId != null) {
            wrapper.eq(AdminCarModel::getSeriesId, seriesId);
        }
        if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            wrapper.eq(AdminCarModel::getStatus, status);
        }
        wrapper.orderByAsc(AdminCarModel::getId);
        List<AdminCarModel> models = adminCarModelMapper.selectList(wrapper);
        return models.stream().map(this::toModelVO).collect(Collectors.toList());
    }

    @CacheEvict(value = "car-library", allEntries = true)
    public CarModelVO createModel(CarModelDTO dto) {
        AdminCarModel model = new AdminCarModel();
        model.setSeriesId(dto.getSeriesId() != null ? dto.getSeriesId().intValue() : null);
        model.setName(dto.getName());
        model.setYear(dto.getYear());
        model.setGuidePrice(dto.getPrice());
        model.setSortOrder(0);
        model.setStatus("ACTIVE");
        adminCarModelMapper.insert(model);
        return toModelVO(model);
    }

    @CacheEvict(value = "car-library", allEntries = true)
    public CarModelVO updateModel(Long id, CarModelDTO dto) {
        AdminCarModel model = adminCarModelMapper.selectById(id);
        if (model == null) {
            throw new BusinessException(404, "车型不存在");
        }
        if (dto.getSeriesId() != null) model.setSeriesId(dto.getSeriesId().intValue());
        if (dto.getName() != null) model.setName(dto.getName());
        if (dto.getYear() != null) model.setYear(dto.getYear());
        if (dto.getPrice() != null) model.setGuidePrice(dto.getPrice());
        adminCarModelMapper.updateById(model);
        return toModelVO(model);
    }

    @CacheEvict(value = "car-library", allEntries = true)
    public void deleteModel(Long id) {
        AdminCarModel model = adminCarModelMapper.selectById(id);
        if (model == null) {
            throw new BusinessException(404, "车型不存在");
        }
        adminCarModelMapper.deleteById(id);
    }

    // ==================== 批量导入 ====================

    @CacheEvict(value = "car-library", allEntries = true)
    @Transactional
    public int importModels(MultipartFile file) {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            int imported = 0;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Long seriesId = getLongValue(row.getCell(0));
                String name = getStringValue(row.getCell(1));
                Integer year = getIntValue(row.getCell(2));
                BigDecimal price = getBigDecimalValue(row.getCell(3));
                if (seriesId == null || name == null || name.isEmpty()) continue;
                AdminCarModel model = new AdminCarModel();
                model.setSeriesId(seriesId != null ? seriesId.intValue() : null);
                model.setName(name);
                model.setYear(year);
                model.setGuidePrice(price);
                model.setStatus("ACTIVE");
                adminCarModelMapper.insert(model);
                imported++;
            }
            return imported;
        } catch (IOException e) {
            log.error("导入车型Excel失败", e);
            throw new BusinessException(500, "导入失败: " + e.getMessage());
        }
    }

    public byte[] exportTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("车型导入模板");
            Row header = sheet.createRow(0);
            String[] headers = {"车系ID", "车型名称", "年份", "价格"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
            }
            // 示例数据行
            Row example = sheet.createRow(1);
            example.createCell(0).setCellValue(1);
            example.createCell(1).setCellValue("2.0T 豪华版");
            example.createCell(2).setCellValue(2025);
            example.createCell(3).setCellValue(358800.00);
            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            log.error("生成导入模板失败", e);
            throw new BusinessException(500, "生成模板失败");
        }
    }

    // ==================== 工具方法 ====================

    private CarBrandVO toBrandVO(AdminCarBrand brand) {
        CarBrandVO vo = new CarBrandVO();
        vo.setId(brand.getId() != null ? brand.getId().longValue() : null);
        vo.setName(brand.getName());
        vo.setLogoUrl(brand.getLogoUrl());
        vo.setStatus(brand.getStatus());
        vo.setCreatedAt(brand.getCreatedAt());
        return vo;
    }

    private CarSeriesVO toSeriesVO(AdminCarSeries series) {
        CarSeriesVO vo = new CarSeriesVO();
        vo.setId(series.getId() != null ? series.getId().longValue() : null);
        vo.setBrandId(series.getBrandId() != null ? series.getBrandId().longValue() : null);
        vo.setName(series.getName());
        vo.setStatus(series.getStatus());
        vo.setCreatedAt(series.getCreatedAt());
        return vo;
    }

    private CarModelVO toModelVO(AdminCarModel model) {
        CarModelVO vo = new CarModelVO();
        vo.setId(model.getId() != null ? model.getId().longValue() : null);
        vo.setSeriesId(model.getSeriesId() != null ? model.getSeriesId().longValue() : null);
        vo.setName(model.getName());
        vo.setYear(model.getYear());
        vo.setPrice(model.getGuidePrice());
        vo.setStatus(model.getStatus());
        vo.setCreatedAt(model.getCreatedAt());
        return vo;
    }

    private String getStringValue(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

    private Long getLongValue(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case NUMERIC -> (long) cell.getNumericCellValue();
            case STRING -> {
                try {
                    yield Long.parseLong(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    yield null;
                }
            }
            default -> null;
        };
    }

    private Integer getIntValue(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case NUMERIC -> (int) cell.getNumericCellValue();
            case STRING -> {
                try {
                    yield Integer.parseInt(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    yield null;
                }
            }
            default -> null;
        };
    }

    private BigDecimal getBigDecimalValue(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue());
            case STRING -> {
                try {
                    yield new BigDecimal(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    yield null;
                }
            }
            default -> null;
        };
    }
}
