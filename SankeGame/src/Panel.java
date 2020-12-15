import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

//新建画布Panel
public class Panel extends JPanel implements KeyListener, ActionListener {

    // 定义组件

    ImageIcon title;// 定义游戏界面开头栏图标
    ImageIcon body;// 定义蛇身图标
    ImageIcon up;// 定义向上的蛇头图标
    ImageIcon down;// 向下的蛇头图标
    ImageIcon right;// 向右的蛇头图标
    ImageIcon left;// 向左的蛇头图标
    ImageIcon food;// 食物图标
    ImageIcon begin;// 游戏开始图标
    ImageIcon fail;// 游戏结束图标
    BufferedImage bj = null;
    /*
     * 游戏区的背景，定义成bufferedImage类型，该类型对象生成的图片在内存里有一个图像缓冲区，
     * 可利用该缓冲区对图片进行操作，比如设置图像透明与否、大小变换等等
     */

    int len = 3;// 定义蛇的初始长度为3
    int score = 0;// 添加分数
    int[] snakex = new int[650];// 定义蛇身数组x
    int[] snakey = new int[650];// 定义蛇身数组y
    int foodx;// 定义食物横坐标
    int foody;// 定义食物纵坐标
    String fx = "R";// U、L、R、D代表四个方向的蛇头
    boolean isStarted = false;// 定义布尔类型标志游戏是否开始
    boolean isFailed = false;// 蛇是否阵亡标志
    boolean isVictory = false;// 游戏胜利

    Timer timer = new Timer(100, this);// 定义一个时钟,即每间隔100ms，蛇重画一次，连贯起来便形成蛇在运动的视觉效果
    Random rand = new Random();// 引进随机函数，用于随机生成食物
    Clip bgm;// 背景音乐
    Clip over;// 游戏结束音乐
    Clip eat;// 吃到食物时的音乐

    // Panel的构造函数
    public Panel() {
        loadImages();// 加载图片；load方法加载指定的文件名作为作为动态库，文件名参数必须是一个完整的路径名
        initSnake();// 初始化蛇的方法
        this.setFocusable(true);// 设置获取键盘焦点，focusable表示移动光标时是否能聚焦到组件上
        this.addKeyListener(this);// 添加键盘监听器方法，方法摘要有：keyPressed(按下某键时）,keyReleased（释放某键时），keyTyped（键入某键时）
        timer.start();// 启动时钟方法
        loadBGM();// 加载音乐
    }

    // 游戏界面设计画组件， 调用绘制容器组件方法paintComponent
    public void paintComponent(Graphics g) {// 添加一个画笔g
        super.paintComponent(g);// 调用父类方法
        this.setBackground(Color.DARK_GRAY);// 设置画布Panel背景色

        title.paintIcon(this, g, 5, 0);// 用画笔g画title图标
        g.fillRect(5, 35, 775, 525);// 用画笔g画游戏界面黑框
        g.drawImage(bj, 5, 35, this);// 添加游戏界面背景图片
        g.setColor(Color.white);// 设置画笔字体颜色
        g.setFont(new Font("华文行楷", Font.BOLD, 20));// 设置画笔字体类型，加粗，大小
        g.drawString("Len:" + len, 550, 24); // 在窗口右上角画len字符串
        g.drawString("Score:" + score, 630, 24);// 画Score字符串

        // 利用String 变量fx实现动态画蛇头，即可根据蛇运动的方向相应地调整蛇头的方向
        if (fx == "R") {
            right.paintIcon(this, g, snakex[0], snakey[0]);// 蛇头放在头节点，即第一个数组元素中
        } else if (fx == "L") {
            left.paintIcon(this, g, snakex[0], snakey[0]);
        } else if (fx == "U") {
            up.paintIcon(this, g, snakex[0], snakey[0]);
        } else if (fx == "D") {
            down.paintIcon(this, g, snakex[0], snakey[0]);
        }

        // 用循环的方法画蛇身，此时的蛇还是静态的
        for (int i = 1; i < len; i++) {// 不能从0开始，0用来存放蛇头了
            body.paintIcon(this, g, snakex[i], snakey[i]);
        }

        // 画食物
        food.paintIcon(this, g, foodx, foody);

        // 画跳出的游戏开始提示字符
        if (isStarted == false) {
            g.setColor(Color.white);// 重新设置画笔字体颜色
            g.setFont(new Font("arial", Font.BOLD + Font.ITALIC, 45));// 重新设置画笔字体类型，加粗，大小
            g.drawString("Press Space to Your Show Time", 50, 475);// 画它，放在窗口中坐标为（75，300）的地方
            g.setColor(Color.red);
            begin.paintIcon(this, g, 250, 150);
        }

        // 画游戏结束界面
        if (isFailed) {
            g.setColor(Color.white);
            g.setFont(new Font("arial", Font.BOLD, 40));
            g.drawString("Press Space to Restart", 200, 360);
            g.setColor(Color.red);
            g.setFont(new Font("arial", Font.BOLD + Font.ITALIC, 73));// 同时设置粗体与斜体用加号
            g.drawString("Game Over ! ", 200, 250);
            fail.paintIcon(this, g, 0, 380);
        }
    }

    // 初始化蛇，用initSnake方法
    public void initSnake() {
        len = 3;
        snakex[0] = 55;//一定要注意初始坐标的选取，不能随意取，
        snakey[0] = 35;
        snakex[1] = 30;
        snakey[1] = 35;
        snakex[2] = 5;
        snakey[2] = 35;
        foodx = 5 + 25 * rand.nextInt(30);// 横坐标可容纳30个food调用rand方法随机生成
        foody = 35 + 25 * rand.nextInt(21);// 纵坐标可容纳21个food
        fx = "R";// 重新初始化蛇头方向，不然每次一重新开始又立马撞到自己然后over掉！
        score = 0;
    }

    // 自动生成的键盘监听器方法
    @Override
    public void keyTyped(KeyEvent e) {
    }

    // 定义响应键盘按下的行为的方法
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        // 当按下空格键时
        if (keyCode == KeyEvent.VK_SPACE) {
            if (isFailed) {// 先判断当前游戏状态
                isFailed = false;
                initSnake();// 输了，初始化蛇
            } else {
                isStarted = !isStarted;
            }
            repaint();// 重画界面

            // 通过空格键控制音乐的暂停和开始
            if (isStarted) {
                bgm.loop(Clip.LOOP_CONTINUOUSLY);// 循环播放背景音乐
            } else {
                bgm.stop();// 停止播放背景音乐
            }
        }

        // 当按下"——>"键时
        else if (keyCode == KeyEvent.VK_LEFT) {
            fx = "L";
        }
        // 当按下"<——"键时
        else if (keyCode == KeyEvent.VK_RIGHT) {
            fx = "R";
        }
        // 当按下向上键时
        else if (keyCode == KeyEvent.VK_UP) {
            fx = "U";
        }
        // 当按下向下键时
        else if (keyCode == KeyEvent.VK_DOWN) {
            fx = "D";
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    // 定义操作事件的方法
    @Override
    public void actionPerformed(ActionEvent e) {// 事件监听器ActionEvent在动作发生时调用，对应的处理方法为actionPerformed

        // 当游戏已经开始并且没有结束时，用循环的方法画蛇身，此处可实现蛇的动态运动效果
        if (isStarted && !isFailed) {
            // 递归方法，每间隔一个时钟周期，蛇移动一格，后一节身体的坐标变成前一节身体的坐标，蛇头直接向前移动一格，一直画一直画
            for (int i = len - 1; i > 0; i--) {
                snakex[i] = snakex[i - 1];
                snakey[i] = snakey[i - 1];
            }

            // 当蛇吃到食物时身体会变长
            if (snakex[0] == foodx && snakey[0] == foody) {
                eat.loop(2);
                foodx = 5 +25*rand.nextInt(30);// 食物被吃掉后继续随机生成
                foody = 35 +25*rand.nextInt(21);// 21是用黑框宽度525除以每一格的宽度25得来的，算对很重要，不然运行起来会有毛病
                len++;// 蛇身变长一节
                score = score + 100;// 每吃掉一个食物，分数增加100
            }

            // 定义蛇移动的方法
            if (fx == "R") {
                snakex[0] = snakex[0] + 25;// 向右移动就横坐标加25
                if (snakex[0] > 755)// 当向右运动超出边界时，蛇死，游戏结束
                    Failed();
            } else if (fx == "L") {
                snakex[0] = snakex[0] - 25;
                if (snakex[0] < 5)
                    Failed();
            } else if (fx == "U") {
                snakey[0] = snakey[0] - 25;
                if (snakey[0] < 35)
                    Failed();
            } else if (fx == "D") {
                snakey[0] = snakey[0] + 25;
                if (snakey[0] > 535)
                    Failed();
            }


            // 当蛇碰到自身时,游戏结束
            for (int i = 1; i < len; i++) {// for循环的作用是不管蛇头碰到哪一节身体都会over，遍历一遍看看是哪一节
                if (snakex[i] == snakex[0] && snakey[i] == snakey[0]) {
                    Failed();
                }
            }
            repaint();// 每一次行为后都要重画以实现动态性与实时性
        }
        timer.start();// 时钟开始
    }

    // 游戏结束时的方法
    private void Failed() {
        over.loop(1);// 游戏结束时播放死亡音乐
        bgm.stop();// 游戏结束时使背景音乐暂停
        isFailed = true;// 设置游戏处于结束状态
    }

    // 加载音乐的方法
    private void loadBGM() {
        InputStream is;// 定义字节输入流变量
        AudioInputStream ais;// 该类为inputstream的直接子类，用于读取音频
        FloatControl gainControl;// 该类提供对一系列浮点值的控制，此处定义用来控制音频音量大小

        try {
            bgm = AudioSystem.getClip();
            is = this.getClass().getClassLoader().getResourceAsStream("sound/background.wav");// 通过类加载器找到字节流
            ais = AudioSystem.getAudioInputStream(is);// 转换成音频字节流
            bgm.open(ais);
            gainControl = (FloatControl) bgm.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-10.0f);// 控制调整bgm的音量大小

            eat = AudioSystem.getClip();
            is = this.getClass().getClassLoader().getResourceAsStream("sound/eat.wav");
            ais = AudioSystem.getAudioInputStream(is);
            eat.open(ais);
            gainControl = (FloatControl) eat.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(3.0f);

            over = AudioSystem.getClip();
            is = this.getClass().getClassLoader().getResourceAsStream("sound/over.wav");
            ais = AudioSystem.getAudioInputStream(is);
            over.open(ais);
            gainControl = (FloatControl) over.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(3.0f);

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 加载图片的方法
    private void loadImages() {
        InputStream is;// 定义输入流对象is
        try {// 之所以整这么麻烦是为了把图片等资源都整合在当前SnakeGame项目的一个文件夹里，方便后面将整个源代码导出成exe程序
            is = getClass().getClassLoader().getResourceAsStream("images/title.png");// 通过类加载器查找图片
            title = new ImageIcon(ImageIO.read(is));// 让title画出其相对应的图标

            is = getClass().getClassLoader().getResourceAsStream("images/bj.jpg");
            bj = ImageIO.read(is);

            is = getClass().getClassLoader().getResourceAsStream("images/body.png");
            body = new ImageIcon(ImageIO.read(is));

            is = getClass().getClassLoader().getResourceAsStream("images/up.png");
            up = new ImageIcon(ImageIO.read(is));

            is = getClass().getClassLoader().getResourceAsStream("images/down.png");
            down = new ImageIcon(ImageIO.read(is));

            is = getClass().getClassLoader().getResourceAsStream("images/right.png");
            right = new ImageIcon(ImageIO.read(is));

            is = getClass().getClassLoader().getResourceAsStream("images/left.png");
            left = new ImageIcon(ImageIO.read(is));

            is = getClass().getClassLoader().getResourceAsStream("images/food.png");
            food = new ImageIcon(ImageIO.read(is));

            is = getClass().getClassLoader().getResourceAsStream("images/fail.png");
            fail = new ImageIcon(ImageIO.read(is));

            is = getClass().getClassLoader().getResourceAsStream("images/victory.png");
            begin = new ImageIcon(ImageIO.read(is));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

