package com.github.somprasongd.jasperreports.pdf.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class JasperDto {

    @NotBlank
    private String name;
    @NotBlank
    private String url;
    @NotBlank
    private long modified_at;




}
