package com.code.rpc.server.tcp;

import com.code.rpc.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;


/**
 * TCP 消息处理器包装
 * 装饰者模式 （使用 recordParse 对原有 buffer 处理能力进行增强，解决半包和粘包问题）
 *
 * @author Liang
 * @create 2024/3/23
 */
public class TcpBufferHandlerWrapper implements Handler<Buffer> {

    /**
     * 解析器，用于解决半包、粘包问题
     */
    private final RecordParser recordParser;

    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler) {
        this.recordParser = initRecordParser(bufferHandler);
    }

    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }

    /**
     * 初始化解析器
     *
     * @param bufferHandler 处理器
     * @return 解析器
     */
    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
        // 构造 parser
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);

        parser.setOutput(new Handler<Buffer>() {
            // 初始化
            int size = -1;
            // 保存一次完整的读取结果（消息头 + 消息体）
            Buffer resultBuffer =  Buffer.buffer();

            @Override
            public void handle(Buffer buffer) {
                // 1.每次循环，先读取消息头
                if (-1 == size) {
                    // 从消息头中读取消息体长度(第13个字节开始，一个整形数据)，调整parser的长度，读取变长消息体
                    size = buffer.getInt(13);
                    parser.fixedSizeMode(size);
                    // 将消息头(固定长度17字节)保存到读取结果
                    resultBuffer.appendBuffer(buffer);
                } else {
                    // 2.读取消息体
                    // 将消息体保存到读取结果
                    resultBuffer.appendBuffer(buffer);
                    // 已读取一次为完整 Buffer，执行处理
                    bufferHandler.handle(resultBuffer);
                    // 重置一轮
                    parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    size = -1;
                    resultBuffer = Buffer.buffer();
                }
            }
        });

        return parser;
    }
}
