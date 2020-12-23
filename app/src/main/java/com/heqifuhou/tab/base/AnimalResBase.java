package com.heqifuhou.tab.base;

public interface AnimalResBase {
	void setPreInAndOutAnimalRes(final int nInAnimalRes,final int nOutAnimalRes);
	void setNextInAndOutAnimalRes(final int nBackInAnimalRes,final int nBackOutAnimalRes);
	int getPreInAnimalResID();
	int getPreOutAnimalResID();
	int getNextInAnimalResID();
	int getNextOutAnimalResID();
}
