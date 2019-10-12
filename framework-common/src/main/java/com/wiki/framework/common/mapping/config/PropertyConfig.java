package com.wiki.framework.common.mapping.config;

/**
 * 映射对象的属性定义
 *
 * @author Thomason
 * Date: 11-4-20
 * Time: 下午5:20
 * @version 1.0
 */
@SuppressWarnings({"RedundantIfStatement"})
public class PropertyConfig implements Cloneable {
	/**
	 * 源对象属性名称
	 */
	private String srcPropertyName;
	/**
	 * 目标对象属性名称
	 */
	private String destPropertyName;
	/**
	 * 源对象属性类型
	 */
	private int srcPropertyType;
	/**
	 * 目标对象属性类型
	 */
	private int destPropertyType;
	/**
	 * 是否是计算字段
	 */
	private boolean isFormula;
	/**
	 * 计算表达式
	 */
	private String script;

	public String getSrcPropertyName() {
		return srcPropertyName;
	}

	public void setSrcPropertyName(String srcPropertyName) {
		this.srcPropertyName = srcPropertyName;
	}

	public String getDestPropertyName() {
		return destPropertyName;
	}

	public void setDestPropertyName(String destPropertyName) {
		this.destPropertyName = destPropertyName;
	}

	public int getSrcPropertyType() {
		return srcPropertyType;
	}

	public void setSrcPropertyType(int srcPropertyType) {
		this.srcPropertyType = srcPropertyType;
	}

	public int getDestPropertyType() {
		return destPropertyType;
	}

	public void setDestPropertyType(int destPropertyType) {
		this.destPropertyType = destPropertyType;
	}

	public boolean isFormula() {
		return isFormula;
	}

	public void setFormula(boolean formula) {
		isFormula = formula;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	@Override
	protected PropertyConfig clone() throws CloneNotSupportedException {
		return (PropertyConfig) super.clone();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		PropertyConfig that = (PropertyConfig) o;

		if (destPropertyType != that.destPropertyType) {
			return false;
		}
		if (srcPropertyType != that.srcPropertyType) {
			return false;
		}
		if (destPropertyName != null ? !destPropertyName.equals(that.destPropertyName) : that.destPropertyName != null) {
			return false;
		}
		if (srcPropertyName != null ? !srcPropertyName.equals(that.srcPropertyName) : that.srcPropertyName != null) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int result = srcPropertyName != null ? srcPropertyName.hashCode() : 0;
		result = 31 * result + (destPropertyName != null ? destPropertyName.hashCode() : 0);
		result = 31 * result + srcPropertyType;
		result = 31 * result + destPropertyType;
		return result;
	}
}
