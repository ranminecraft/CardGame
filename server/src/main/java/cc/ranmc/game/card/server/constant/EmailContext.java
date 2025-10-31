package cc.ranmc.game.card.server.constant;

public class EmailContext {
    private static final String START = """
            <div style="background-color: #364f6b;display: flex;justify-content: center;align-items: center;flex-direction: column;height: 100%">
                <br>
                <p style="color: #eaeaea"><br>
            """;

    private static final String END = """
            <br>
            <br>官网地址 <a style="color: #3fc1c9" href='https://www.ranmc.cc'>www.ranmc.cc</a>
            <br>加入群聊 <a style="color: #3fc1c9" href='https://qm.qq.com/q/hpYH0xIsuY'>182925855</a>
            <br>系统邮件，请勿回复<br>祝你游戏愉快<br>阿然 呈上</p><br></div>
            """;

    public static String VERIFY = START + """
            %name% ，您好！
            <br>您的验证码为
            <br><br>
            <a style="color: #fc5185; font-weight: bold; font-size:20px">%key%</a>
            <br><br>验证码将在10分钟之后过期""" + END;
}
