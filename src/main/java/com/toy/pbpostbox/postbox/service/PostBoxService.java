package com.toy.pbpostbox.postbox.service;

import com.toy.pbpostbox.common.domain.Location;
import com.toy.pbpostbox.postbox.domain.PostBox;
import com.toy.pbpostbox.postbox.dto.PostBoxDto;
import com.toy.pbpostbox.postbox.repository.PostBoxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostBoxService {

    private final PostBoxRepository postBoxRepository;

    public PostBoxDto.Res savePostBox(String uid, PostBoxDto.Req req) {
        return PostBoxDto.Res.of(postBoxRepository.save(req.toEntity(uid)));
    }

    @Transactional
    public void deletePostBox(String uid) {
        postBoxRepository.deleteByUid(uid);
    }

    public List<PostBoxDto.Res> getPostBox(String uid) {
        return postBoxRepository.findByUid(uid).stream().map(PostBoxDto.Res::of).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostBoxDto.Res> getSquareMapPostBoxList(double baseLatitude, double baseLongitude, double distance) {

        // 북동쪽 좌표 구하기
        Location northEast = GeometryUtil.calculate(baseLatitude, baseLongitude, distance, Direction.NORTH_EAST);

        // 남서쪽 좌표 구하기
        Location southWest = GeometryUtil.calculate(baseLatitude, baseLongitude, distance, Direction.SOUTH_WEST);

        double x1 = northEast.getLatitude();
        double y1 = northEast.getLongitude();
        double x2 = southWest.getLatitude();
        double y2 = southWest.getLongitude();

        String lineString = String.format("LINESTRING(%f %f, %f %f)", x1, y1, x2, y2);

        List<PostBox> squareMapPostBoxList = postBoxRepository.getSquareMapPostBoxList(lineString);

        return squareMapPostBoxList.stream().map(PostBoxDto.Res::of).collect(Collectors.toList());
    }
}
