package listclass;

public class Subject {
    public int ImageRes_1;//图像资源ID1
    public String Subject_Name;//课程名
    public int ImageRes_2;//图像资源ID2

    public Subject(int itemImageResId_1, String itemTitle, int itemImageResId_2) {
        this.ImageRes_1 = itemImageResId_1;
        this.Subject_Name = itemTitle;
        this.ImageRes_2 = itemImageResId_2;
    }

    public String getSubject_Name() {
        return Subject_Name;
    }
}
