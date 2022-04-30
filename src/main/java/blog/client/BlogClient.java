package blog.client;

import com.google.protobuf.Empty;
import com.proto.blog.Blog;
import com.proto.blog.BlogId;
import com.proto.blog.BlogServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class BlogClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50053)
                .usePlaintext()
                .build();

        run(channel);

        System.out.println("Shutting down");
        channel.shutdown();
    }

    private static void run(ManagedChannel channel) {
        BlogServiceGrpc.BlogServiceBlockingStub stub = BlogServiceGrpc.newBlockingStub(channel);

        BlogId blogId = createBlog(stub);

        if (blogId == null) {
            return;
        }

        readBlog(stub, blogId);
        updateBlog(stub, blogId);
        listBlog(stub);
        deleteBlog(stub, blogId);
    }

    private static BlogId createBlog(BlogServiceGrpc.BlogServiceBlockingStub stub) {
        try {
            BlogId response = stub.createBlog(
                    Blog
                            .newBuilder()
                            .setAuthor("Thanh")
                            .setTitle("New Blog")
                            .setContent("Hello Grpc")
                            .build()
            );

            System.out.println("Blog create: " + response.getId());
            return response;
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Blog readBlog(BlogServiceGrpc.BlogServiceBlockingStub stub, BlogId blogId) {
        try {
            Blog response = stub.readBlog(blogId);

            System.out.println("Blog read: " + response);
            return response;
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void updateBlog(BlogServiceGrpc.BlogServiceBlockingStub stub, BlogId blogId) {
        try {
            Blog newBlog = Blog.newBuilder()
                    .setId(blogId.getId())
                    .setAuthor("Thanh 1")
                    .setTitle("Update title")
                    .setContent("update content")
                    .build();

            stub.updateBlog(newBlog);
            System.out.println("Blog updated: " + newBlog);
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
    }

    private static void listBlog(BlogServiceGrpc.BlogServiceBlockingStub stub) {
        System.out.println("listBlog");
        try {
            stub.listBlog(Empty.getDefaultInstance()).forEachRemaining(System.out::println);
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
    }

    private static void deleteBlog(BlogServiceGrpc.BlogServiceBlockingStub stub, BlogId blogId) {
        try {
            stub.deleteBlog(blogId);
            System.out.println("Blog delete: " + blogId);
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
    }
}
