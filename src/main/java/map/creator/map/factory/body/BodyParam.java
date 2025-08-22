package map.creator.map.factory.body;

import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import map.creator.map.utils.exception.NotInitializedObjectException;

/// <i>BodyParam</i> - it is instruction for creating some body for object.
public class BodyParam{

    /// Sets the form body - More details in class FormBody
    /// @see FormBody
    public final FormBody formBody;

    /// Parameters to create Body
    /// (P.S: The field "position" will be ignored, because {@link BodyFactory} determines the position regarding all position fixtures.)
    /// @see BodyDef
    public final BodyDef bodyDef;

    /// Parameters to create Fixture
    /// (P.S: there is no need to set the "shape" - it will be determined by "Shape2D in {@link BodyFactory}")
    /// @see FixtureDef
    public final FixtureDef fixtureDef;

    /// Bounds object
    public final Shape2D bounds;

    /// Key/Id/name to object.
    public final UserData userData;

    /// Whether the figure should be closed.
    /// (In CHAIN form only)
    /// @see FormBody
    public boolean isLooping;

    public BodyParam(FormBody formBody, BodyDef bodyDef, FixtureDef fixtureDef, Shape2D bounds, UserData userData) {
        this.formBody = formBody;
        this.bodyDef = bodyDef;
        this.fixtureDef = fixtureDef;
        this.bounds = bounds;
        this.userData = userData;
    }

    private BodyParam(BodyParamBuilder builder) {
        formBody = builder.formBody;
        bodyDef = builder.bodyDef;
        fixtureDef = builder.fixtureDef;
        isLooping = builder.isLooping;
        bounds = builder.bounds;
        userData = builder.userData;
    }

    public static class BodyParamBuilder {
         protected FormBody formBody;
         protected BodyDef bodyDef;
         protected FixtureDef fixtureDef;
         protected boolean isLooping = false;
         protected Shape2D bounds;
         protected UserData userData;

        public BodyParamBuilder formBody(FormBody formBody) {
            this.formBody = formBody;
            return this;
        }

        public BodyParamBuilder bodyDef(BodyDef bodyDef) {
            this.bodyDef = bodyDef;
            return this;
        }

        public BodyParamBuilder fixtureDef(FixtureDef fixtureDef) {
            this.fixtureDef = fixtureDef;
            return this;
        }

        public BodyParamBuilder looping(boolean looping) {
            isLooping = looping;
            return this;
        }

        public BodyParamBuilder bounds(Shape2D bounds) {
            this.bounds = bounds;
            return this;
        }

        public BodyParamBuilder userData(UserData userData) {
            this.userData = userData;
            return this;
        }

        /// Build BodyParam
        /// @throws NotInitializedObjectException when "validate()" method returned false
        public BodyParam build() {
            BodyParam param;

            if (validate()) param = new BodyParam(this);
            else throw new NotInitializedObjectException("FormBody, BodyType, FixtureDef or Shape2D not been initialized!");

            return param;
        }

        /// Verifies that objects are initialized
        /// @return true if formBody, bodyDef, fixtureDef, bounds(Shape2D) and userData not null. False else.
        private boolean validate() {
            return !(formBody == null || bodyDef == null || fixtureDef == null || bounds == null || userData == null);
        }
    }
}
