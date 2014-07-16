package com.omniture.api;

public class ResultDatas {
	private int pv;
	private int uv;
	private String TRFC;

	ResultDatas() {
	}

	ResultDatas(int pv, int uv, String TRFC) {
		this.pv = pv;
		this.uv = uv;
		this.TRFC = TRFC;
	}

	public int getPv() {
		return pv;
	}

	public void setPv(int pv) {
		this.pv = pv;
	}

	public int getUv() {
		return uv;
	}

	public void setUv(int uv) {
		this.uv = uv;
	}

	public String getTRFC() {
		return TRFC;
	}

	public void setTRFC(String tRFC) {
		TRFC = tRFC;
	}

	@Override
	public String toString() {
		return "ResultDatas [pv=" + pv + ", uv=" + uv + ", TRFC=" + TRFC + "]";
	}

}
