package backend.symbols;

import frontend.parser.specificUnit.Param;

public class MipsSymbol {
    private String name;
//    private boolean isInReg;
    private int regIndex=-1; //专门给临时变量
    private boolean hasRam;
    private int base;
    private int offset;
    private boolean isTemp;
    private boolean isUsed=false; //临时寄存器使用过就可以被释放
    private int dimensionNum=0;
    private boolean isParam=false;

    //不给定维度参数，默认常变量
    public  MipsSymbol(String name,int base,int offset,boolean isTemp){
        this.name=name;
        this.base=base;
        this.offset=offset;
        this.isTemp=isTemp;
        this.dimensionNum=0;
    }
    //全局0/1维变量
    public MipsSymbol(String name,int base,int offset,boolean isTemp,int dimensionNum){
        this(name,base,offset,isTemp);
        this.dimensionNum=dimensionNum;
        this.hasRam=true;
    }
    //创建局部0/1维变量
    public MipsSymbol(String name,int dimensionNum){ //还没分配内存
        this(name,30,-1,false);
        this.dimensionNum=dimensionNum;
    }

    //创建临时变量
    public MipsSymbol(String name,boolean isTemp){
        this(name,30,-1,isTemp);
        this.dimensionNum=0;
    }

    public int getRegIndex(){
        return regIndex;
    }

    public int getDimensionNum(){
        return dimensionNum;
    }
    public void setOffset(int offset){
        this.offset=offset;
    }
    public void setParam(boolean isParam){
        this.isParam=isParam;
    }
    public boolean isParam(){
        return isParam;
    }

    public String getName(){
        return name;
    }

    public void setDimensionNum(int dimensionNum){
        this.dimensionNum=dimensionNum;
    }
    public int getBase(){
        return base;
    }
    public int getOffset(){
        return offset;
    }
    public void setBase(int base){
        this.base=base;
    }
    public void setHasRam(boolean hasRam){
        this.hasRam=hasRam;
    }
    public void setRegIndex(int regIndex){
        this.regIndex=regIndex;
    }
    public boolean isTemp(){
        return isTemp;
    }
    public boolean isUsed(){
        return isUsed;
    }
    public void setUsed(boolean isUsed){
        this.isUsed=isUsed;
    }

    public boolean hasRam(){
        return hasRam;
    }


}
