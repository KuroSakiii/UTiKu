package listclass;

public class Que {
    public int ImageRes;//图像资源ID
    public String No;//题号
    public String Content;//所属课程
    public String Que;//题目内容

    public Que(int itemImageResId, String no, String content, String que) {
        this.ImageRes = itemImageResId;
        this.No = no;
        this.Content = content;
        this.Que = que;
    }

    public String getNo() {
        return No;
    }
}
