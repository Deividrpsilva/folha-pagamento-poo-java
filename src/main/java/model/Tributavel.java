package model;

/**
 * Contrato para categorias de funcionário sobre cuja remuneração a empresa
 * calcula e retém tributos ou descontos.
 */
public interface Tributavel {

    /**
     * @return o valor total dos tributos/descontos aplicáveis à remuneração
     */
    double calcularTributos();
}
