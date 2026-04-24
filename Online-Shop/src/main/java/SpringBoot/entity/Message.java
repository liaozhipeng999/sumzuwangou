package SpringBoot.entity;

import io.renren.common.utils.Result;
import lombok.Data;

import java.util.List;

@Data
public class Message {
    private GoodEntity goodEntity;
    private List<PicsEntity> picsEntity;
    private Result result;
}
