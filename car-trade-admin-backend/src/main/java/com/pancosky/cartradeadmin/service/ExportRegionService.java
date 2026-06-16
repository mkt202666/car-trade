package com.pancosky.cartradeadmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pancosky.cartradeadmin.common.BusinessException;
import com.pancosky.cartradeadmin.dto.ExportRegionDTO;
import com.pancosky.cartradeadmin.entity.ExportRegion;
import com.pancosky.cartradeadmin.mapper.ExportRegionMapper;
import com.pancosky.cartradeadmin.vo.ExportRegionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExportRegionService {

    @Autowired
    private ExportRegionMapper exportRegionMapper;

    @Autowired
    private ObjectMapper objectMapper;

    public List<ExportRegionVO> listAll() {
        LambdaQueryWrapper<ExportRegion> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(ExportRegion::getGroupKey).orderByAsc(ExportRegion::getCode);
        List<ExportRegion> regions = exportRegionMapper.selectList(wrapper);
        return regions.stream().map(this::toVO).collect(Collectors.toList());
    }

    public Long create(ExportRegionDTO dto) {
        // Validate JSON fields
        validateJson(dto.getConstraints(), "constraints");
        validateJson(dto.getRequirements(), "requirements");

        // Check code uniqueness
        LambdaQueryWrapper<ExportRegion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExportRegion::getCode, dto.getCode());
        Long count = exportRegionMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new BusinessException("国家代码 " + dto.getCode() + " 已存在");
        }

        ExportRegion entity = new ExportRegion();
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setRegionGroup(dto.getGroup());
        entity.setGroupKey(dto.getGroupKey());
        entity.setIcon(dto.getIcon());
        entity.setFlag(dto.getFlag());
        entity.setConstraints(dto.getConstraints() != null ? dto.getConstraints() : "[]");
        entity.setRequirements(dto.getRequirements() != null ? dto.getRequirements() : "[]");
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        exportRegionMapper.insert(entity);
        log.info("Created export region: code={}, id={}", entity.getCode(), entity.getId());
        return entity.getId();
    }

    public void update(Long id, ExportRegionDTO dto) {
        ExportRegion entity = exportRegionMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(404, "导出地区不存在");
        }

        // Validate JSON fields
        validateJson(dto.getConstraints(), "constraints");
        validateJson(dto.getRequirements(), "requirements");

        // Check code uniqueness (excluding self)
        if (dto.getCode() != null && !dto.getCode().equals(entity.getCode())) {
            LambdaQueryWrapper<ExportRegion> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ExportRegion::getCode, dto.getCode());
            wrapper.ne(ExportRegion::getId, id);
            Long count = exportRegionMapper.selectCount(wrapper);
            if (count != null && count > 0) {
                throw new BusinessException("国家代码 " + dto.getCode() + " 已存在");
            }
            entity.setCode(dto.getCode());
        }

        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getGroup() != null) entity.setRegionGroup(dto.getGroup());
        if (dto.getGroupKey() != null) entity.setGroupKey(dto.getGroupKey());
        if (dto.getIcon() != null) entity.setIcon(dto.getIcon());
        if (dto.getFlag() != null) entity.setFlag(dto.getFlag());
        if (dto.getConstraints() != null) entity.setConstraints(dto.getConstraints());
        if (dto.getRequirements() != null) entity.setRequirements(dto.getRequirements());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        entity.setUpdatedAt(LocalDateTime.now());

        exportRegionMapper.updateById(entity);
        log.info("Updated export region: id={}, code={}", id, entity.getCode());
    }

    public void delete(Long id) {
        ExportRegion entity = exportRegionMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(404, "导出地区不存在");
        }
        exportRegionMapper.deleteById(id);
        log.info("Deleted export region: id={}, code={}", id, entity.getCode());
    }

    private ExportRegionVO toVO(ExportRegion entity) {
        ExportRegionVO vo = new ExportRegionVO();
        vo.setId(entity.getId());
        vo.setCode(entity.getCode());
        vo.setName(entity.getName());
        vo.setGroup(entity.getRegionGroup());
        vo.setGroupKey(entity.getGroupKey());
        vo.setIcon(entity.getIcon());
        vo.setFlag(entity.getFlag());
        vo.setConstraints(entity.getConstraints());
        vo.setRequirements(entity.getRequirements());
        vo.setStatus(entity.getStatus());
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());
        return vo;
    }

    private void validateJson(String json, String fieldName) {
        if (json == null || json.isBlank()) {
            return;
        }
        try {
            objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new BusinessException(fieldName + " 不是合法的JSON格式");
        }
    }
}
