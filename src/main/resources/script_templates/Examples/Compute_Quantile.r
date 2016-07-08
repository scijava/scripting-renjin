# @Dataset image
# @OUTPUT double[] result

# get the image size
size <- image$imgPlus$size()

# we convert our image to a vector for use in R
vector <- vector(mode='numeric', length=size)

# Pixelwise conversion
# WARNING: this is very slow
ra <- image$imgPlus$randomAccess()
for (i in 1:size)
{
vector[i - 1] <- ra$get()$get()
ra$fwd(as.integer(0))
}

# Now we can compute statistics on our vector
result <- quantile(vector, c(0.05, .48, .87))
